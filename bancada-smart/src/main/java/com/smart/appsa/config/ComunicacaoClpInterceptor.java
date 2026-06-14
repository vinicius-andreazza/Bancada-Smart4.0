package com.smart.appsa.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.smart.appsa.service.clp.EstoqueComm;
import com.smart.appsa.service.clp.ExpedicaoComm;
import com.smart.appsa.service.clp.MontagemComm;
import com.smart.appsa.service.clp.ProcessoComm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ComunicacaoClpInterceptor implements HandlerInterceptor {

    private final EstoqueComm estoqueComm;
    private final ProcessoComm processoComm;
    private final MontagemComm montagemComm;
    private final ExpedicaoComm expedicaoComm;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String method = request.getMethod();

        // Requisições de leitura (e preflight CORS) passam livremente.
        if (HttpMethod.GET.matches(method)
                || HttpMethod.HEAD.matches(method)
                || HttpMethod.OPTIONS.matches(method)) {
            return true;
        }

        // Alterações só são permitidas com os 4 CLPs em comunicação.
        if (!(estoqueComm.isConnected()
                && processoComm.isConnected()
                && montagemComm.isConnected()
                && expedicaoComm.isConnected())) {
            response.setStatus(HttpStatus.CONFLICT.value());
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(
                    "Operação bloqueada: a aplicação não está em comunicação com todos os CLPs. "
                            + "Inicie a comunicação em /api/smart/start.");
            return false;
        }

        return true;
    }
}