package com.xx;

import com.sun.net.httpserver.HttpServer;
import com.xx.probes.LivenessHandler;
import com.xx.probes.StartupHandler;
import com.xx.reconciler.SpringBootOperatorReconciler;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.api.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Runner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private final static String DEFAULT_KUEB_CONFIG = """
            apiVersion: v1
            clusters:
            - cluster:
                certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCVENDQWUyZ0F3SUJBZ0lJQ28rS1F1Zkc5eTh3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TkRBMU1qQXdNelEzTWpGYUZ3MHpOREExTVRnd016VXlNakZhTUJVeApFekFSQmdOVkJBTVRDbXQxWW1WeWJtVjBaWE13Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLCkFvSUJBUURqTlhFbVBuS000MzVPWWVlV3FXa0hkWlZFdEtuZmo4Q1B5NGd4dUh3NE9aREQ2b0xxUDNZdFpNaVgKMmNEcXRMc3BrVEoyak1mcHpzVUlnZ3Y3U3VkRU5ac3JNbjNmWHExazZ3MUc4ellCNC9sMTJSd1IwcDZ3VS9RegpjSnhPWkZuRWpjeUJVWUw1c1R1MUVVYjhhS3ZwYWhZdHR0bUhhT3UyRFZCWmx4RC84ZjBHazNseE1ja0w3QWpMClJQVVpUQ2RhNC93RDc3TUQ0OHF1dkZEcGxLcjhnSEk3eStrMTZ1ZmpKN3FrRW83MXlMdEdTbDZkOERvU1NRaWMKWnJYNjl4Y1pVV3pTam91aUtpdUVSdjZCSjFzdlBQSVdNR0J4Z0VRYjk3UzRhOUFSc1g2Y3JjdWZBYjlBMlIrMwp6dTlSODZvOE5hRmNFc3lNdjFSTSs2c2VJeURYQWdNQkFBR2pXVEJYTUE0R0ExVWREd0VCL3dRRUF3SUNwREFQCkJnTlZIUk1CQWY4RUJUQURBUUgvTUIwR0ExVWREZ1FXQkJSKzZZZDJyU1M5UEU1d0lhcHdpaTVXM2UwZEJ6QVYKQmdOVkhSRUVEakFNZ2dwcmRXSmxjbTVsZEdWek1BMEdDU3FHU0liM0RRRUJDd1VBQTRJQkFRQ0lmTjMvUTZBVApRbjVQSHg5WjVocmszbE1HN3hsdDl4ekplOWtaWjJpT24wOWd6ZXo3YjJYYmVCUVNaWmRKNEdKTUxkd0ZkVjNwCjYzQ3hsL1pVSlFrbnJZVTNDT0tPNmF3SnUrbjkwVnQwNGYyWlNBaWF0SXEvTG5IUTBoRWdST3NPK1d0eElpeTMKOXhvNHExL0tZNjI3dEdNQ3llTjNWN2ozUno0aGxwaFg5U2NGSXl3bGFQRGJ1VDVrcCtUMk42cTlPUFY4U0ovZAp0ZHlKTTV3dE94anFkbW5PNVMvZkFnUkVRVUNBQmxzZVg0Y0d3Y2dpV2diMkFzY1JsOTVYaHUxS3h0REhiMWptCldQSGE5KzZBZWdUZkxVcDVjdVVBUXRmVVBidjdjSVQ1dllIbGtZNndNdzFsQTNTTkF3RmppQndGcFFoZkx2dm8KdVZqQnhsSEE3S0dSCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K
                server: https://192.168.101.135:6443
              name: kubernetes
            contexts:
            - context:
                cluster: kubernetes
                user: kubernetes-admin
              name: kubernetes-admin@kubernetes
            current-context: kubernetes-admin@kubernetes
            kind: Config
            preferences: {}
            users:
            - name: kubernetes-admin
              user:
                client-certificate-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURJVENDQWdtZ0F3SUJBZ0lJVEl2RFF1THRZUjR3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TkRBMU1qQXdNelEzTWpGYUZ3MHlOVEExTWpBd016VXlNak5hTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQTVJcXE3bkpHQStrSUY2TnUKNmN3LzhveWpGVmpvNEJaV09Ja3FUZUpPQktIQmMycUZpMFVTNHBMNzFGUllJL2dUWlBubWQ0WDkzU0FlR0NYQwpkMjJmby9IdXFUUHR6S053YXBDTjYyQmZZUXFKYnAwcUJNZFJ1bkNwb0dYM0dzaHAyUUVCZUt0Rm5DdzNWZEJrCjI0amNpK21JOUprODFMZ3FkRVNpcVBpSk5nVHZ0SzJBL0hBUnN2RE9GOWR5dmZmcEF3ck1BVHdXWHR2SmR5M2oKOGdBQTQyZzJrNGVZL29sZGZhWjcrUVZNMTZ5aC9FQVpwazVVTUdRbElHcmJWV1p4N1d4SmhjNW12RzRlTDBpTQpqbXduci85cFlIQk1mTlQwOFlwTFBjc3ZEUlVYZHNVSWFDQUZPdDhBczlYQ3lNWExETjBQRUtiUkxFWFB0eWFyClNzeHpyd0lEQVFBQm8xWXdWREFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RBWURWUjBUQVFIL0JBSXdBREFmQmdOVkhTTUVHREFXZ0JSKzZZZDJyU1M5UEU1d0lhcHdpaTVXM2UwZApCekFOQmdrcWhraUc5dzBCQVFzRkFBT0NBUUVBVVBCdkk5d2Z4Z1AwNEJ2QlB2bENOa0dWbHJzVmNwM1BkRjdFCmNzb05RbndqR0t1cFFNcXEzbDVKSHU1MEZhbjA2aVRLZ294YjJlaElNaENWY0psOXJZamdzWFFBcGJTQjlMRW4KcURMZnNzVXArUVNhR3dHdUNuSFdZNUVOVFFFcWNKcXRwOXIwd0VWajB5WjRrbVh0SEcyaVlTZmdHN1FORzFqMwpvNXdXZ0pEQ3lpVkU2SWZjZ3dzSEpKdEFBNXkxb0MzeU5xeFhIL3hSRUtRRWxHTmNNTWI4M2F2VEdscDg0NWNLClBteEdIaElSZWdPVjV3MFVjd2U0UG1iWnZiWFRCOHVqWExOR2tndlRGZElyQ2RQbktCejIrWGNVTGRLdm5HY0sKV0krYUY4MEw2Ukt6bCtwV2Y1c2h3R2IrTHFzaytZR0xIR3dab2RnMXNvck82WkVLNHc9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==
                client-key-data: LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFcEFJQkFBS0NBUUVBNUlxcTduSkdBK2tJRjZOdTZjdy84b3lqRlZqbzRCWldPSWtxVGVKT0JLSEJjMnFGCmkwVVM0cEw3MUZSWUkvZ1RaUG5tZDRYOTNTQWVHQ1hDZDIyZm8vSHVxVFB0ektOd2FwQ042MkJmWVFxSmJwMHEKQk1kUnVuQ3BvR1gzR3NocDJRRUJlS3RGbkN3M1ZkQmsyNGpjaSttSTlKazgxTGdxZEVTaXFQaUpOZ1R2dEsyQQovSEFSc3ZET0Y5ZHl2ZmZwQXdyTUFUd1dYdHZKZHkzajhnQUE0MmcyazRlWS9vbGRmYVo3K1FWTTE2eWgvRUFaCnBrNVVNR1FsSUdyYlZXWng3V3hKaGM1bXZHNGVMMGlNam13bnIvOXBZSEJNZk5UMDhZcExQY3N2RFJVWGRzVUkKYUNBRk90OEFzOVhDeU1YTEROMFBFS2JSTEVYUHR5YXJTc3h6cndJREFRQUJBb0lCQVFEYjlNWHNkaGtDdkc0bwphZ2hlaGxEcGZRZWNqUlNjVDRNdUtIMkp0Z3ZHazNQZlJCOUJXZnFDREZySkVXQS9SZ3hNZStPZkYxdXNUYlFKCkk1WlZZVEZyRlhiUWJJVXJGZ1lrMmZsdEZ6VFU3ZEkxbnBlSlA5aGQ2djZtdkZ3bDc4dEhxcHNpNnh3MDRhMWsKTHd4UzZoYnhwWk5GYnBKSVhQT25NVU9vQW9QMGN0dDlRMkl0OENRNVpOckRMN25qRVZ1bER1czBJamhPdXFDdgpIZWQvMmdIRDZYTUxVNGdUa05ocnVEdm5WUTAxQ1FIWGxJb21hYXFiWXo1RE94L2dQbWY0NHhyMUZVNFdldkFmCnlMYjQzVGVTV0k1MWdvcHFLR1NHV1Byb3NaSEpicitDeC85SlpiUDJqYlY0OTJ1Q1drMURteVV3K1dRRlFWWDQKeS9MN1JvK1pBb0dCQVArdGJsM1Q0UkxKRnY1YTg5RHhYMWJiZGhaODJuYUdlU01QeFZaKzI0SWNZbUlJMDdKdgpJenorVkJEeW1QUlVlYzFGRnVkbDI2OFhhZUR6WVRDakdnbHMzcm5lemZwNTErWHN4d1NTT2h1djZBZ0d0TWdSCk4vYUhRaHkza0M2OG0xVVpEZUx1S3QrVXZYbFY3Q3IyMklXZFhXdElMWG5ZeGtpWkQyNGEyTnFqQW9HQkFPVFUKZVNxUWJqbTRwc1BicE11cE8xeGpTaVJzZ2ErZkdQM2t1YXVrcE53cTQ2a2FWaDJUTEg1NElxMTFnVC90eUR3Wgpib1R4YmpaNGJ4QlZMaTZmS2pCVE5wOEhwQzloamVTYm5WRlpZcmg2blJmNFBFQUtrVUhvWGNLMkh1ZTlPYXBFCmhYQ1FadXdLRG5nZG42eS9WYnNENndkWWVobWd1cHprUjQvMFpIK0ZBb0dBQ0ZOSFlyckJlM0VjMUk5MW5Qb2YKRnA3eXkyeXBXRzZzaHgyK1dVQ3NPU0pmZWIyNzF2V1RlaXIxNWM5Z2Q2UkFpeFNTMkZvU3V6TFIrY2N6QlBocQpvcC91NkxKK2xvZGRKbGw5YVlLeGJiRmVwdFJCRXJxc3pkUTlyTjIxVGpCS0JvMVlUR2xnOEFMUjRKK1JueHlHCmxHTTVaWUxtS0MrTTFWUldra2xCZ20wQ2dZRUFsR2s5b0o2RjdzZlUyUWgyakdSWkRLV1U2NlM0UTdnVEZIQW8KLzZyTUJjT0hYaUNubzR6VmViemUrTk5TSGExUmhiRVUzZ0piOGRZUWFtNWtnblpXZlhzcTNOK3UyOUFRb0NsUgpVZmFBTVpmTWxVUTVoMnVUSkVUWnNyUTl2UjBiN2xOOG5ESDduckV6Q3pKallqM0NFNFlLQS95ZFRFdlRYSGd0CjlhNlRFNlVDZ1lCZWlPeDR4OEJoTzR2MTgzT2tFbVlPNnRlYnlXWTN6aEhJUlJnOE5pZjZyb21UOVRmc2lqdDUKaTYvejR0aCtlT2xKMzlhckNBRXJXNVcrQkVUcEU5dHkrNEc2c2RVcndFWVQ5N055R1p3NkJoWThsbVZMQ2lELwpFK01jbWlKL0tCSFByVmdENStsSTVWRlpPdE5CMDZLdnNSVzgxWXlHSjhaRWhJeUI5V1grYVE9PQotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo=
            """;

    public static void main(String[] args) throws IOException {
        // 自定义 K8S 配置, 运行在远端容器
//        KubernetesClientBuilder builder = new KubernetesClientBuilder();
//        builder.withConfig(
//                Config.fromKubeconfig(DEFAULT_KUEB_CONFIG));
//        KubernetesClient client = builder.build();
//        ConfigurationService configurationService = ConfigurationService.newOverriddenConfigurationService((ov) -> {
//            ov.withKubernetesClient(client);
//        });

//        Operator operator = new Operator(configurationService);
        Operator operator = new Operator();

        operator.register(new SpringBootOperatorReconciler());
        operator.start();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/startup", new StartupHandler(operator));
        // we want to restart the operator if something goes wrong with (maybe just some) event sources
        server.createContext("/healthz", new LivenessHandler(operator));
        server.setExecutor(null);
        server.start();
    }
}
