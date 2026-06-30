package com.smart.appsa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smart.appsa.config.ipconfig.EstoqueIp;
import com.smart.appsa.config.ipconfig.ExpedicaoIp;
import com.smart.appsa.config.ipconfig.MontagemIp;
import com.smart.appsa.config.ipconfig.ProcessoIp;
import com.smart.appsa.config.ipconfig.SeletorTampaIp;

@Configuration
public class AppConfig {
    @Bean
    public SeletorTampaIp seletorTampaIp(){
        return new SeletorTampaIp();
    }

    @Bean
    public EstoqueIp estoqueIp(){
        return new EstoqueIp();
    }

    @Bean
    public ProcessoIp processoIp(){
        return new ProcessoIp();
    }

    @Bean
    public MontagemIp montagemIp(){
        return new MontagemIp();
    }

    @Bean
    public ExpedicaoIp expedicaoIp(){
        return new ExpedicaoIp();
    }

}
