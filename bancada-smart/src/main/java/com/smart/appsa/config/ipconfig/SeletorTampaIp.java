package com.smart.appsa.config.ipconfig;

public class SeletorTampaIp {
    private String endpointSeletorTampa = "http://10.74.241.245/api/move_pos"; 

    public String getEndpointApi() { return endpointSeletorTampa; }
    public void setEndpointApi(String endpointSeletorTampa) { this.endpointSeletorTampa = endpointSeletorTampa; }
}
