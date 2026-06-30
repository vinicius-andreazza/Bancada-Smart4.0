package com.smart.appsa.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("plcEstoqueWriteExecutor")
    public Executor plcEstoqueWriteExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("plc-estoque-write-");
        executor.initialize();
        return executor;
    }

    @Bean("plcExpedicaoWriteExecutor")
    public Executor plcExpedicaoWriteExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("plc-expedicao-write-");
        executor.initialize();
        return executor;
    }

    @Bean("plcIniciarPedido")
    public Executor plcIniciarPedido() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("plc-iniciar-pedido-");
        executor.initialize();
        return executor;
    }
}
