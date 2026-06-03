const tipoPedido = document.getElementById("tipoPedido");
const blocosContainer = document.getElementById("blocosContainer");
const corTampa = document.getElementById("corTampa");
const previewCor = document.getElementById("previewCor");

const ordemProducao = document.getElementById("ordemProducao");
const btnSalvar = document.getElementById("btnSalvar");

function gerarBlocos() {

    blocosContainer.innerHTML = "";

    const quantidade = parseInt(tipoPedido.value);

    for(let i=1;i<=quantidade;i++){

        blocosContainer.innerHTML += `
            <div class="bloco-config">

                <h5>Bloco ${i}</h5>

                <select class="form-select mb-2">
                    <option>Cor da Lâmina</option>
                    <option>Azul</option>
                    <option>Preto</option>
                    <option>Vermelho</option>
                    <option>Branco</option>
                    <option>Amarelo</option>
                    <option>Verde</option>
                </select>

                <select class="form-select mb-2">
                    <option>Padrão 1</option>
                    <option>Padrão 2</option>
                    <option>Padrão 3</option>
                    <option>Padrão 4</option>
                </select>

                <select class="form-select">
                    <option>Esquerda</option>
                    <option>Frente</option>
                    <option>Direita</option>
                </select>

            </div>
        `;
    }
}

function atualizarPreview() {

    let cor = "black";

    if(corTampa.value === "2")
        cor = "red";

    if(corTampa.value === "3")
        cor = "blue";

    previewCor.innerHTML =
        `<div class="preview" style="background:${cor}"></div>`;
}

ordemProducao.addEventListener("input", () => {

    btnSalvar.disabled =
        ordemProducao.value.trim() === "";

});

tipoPedido.addEventListener("change", gerarBlocos);
corTampa.addEventListener("change", atualizarPreview);

gerarBlocos();
atualizarPreview();

async function carregarPedidos(){

    const response =
        await fetch("/api/pedidos");

    const pedidos =
        await response.json();

    const tabela =
        document.getElementById("tabelaPedidos");

    tabela.innerHTML = "";

    pedidos.forEach(pedido => {

        tabela.innerHTML += `
            <tr>
                <td>${pedido.codPedido}</td>
                <td>${pedido.statusPedido}</td>
                <td>
                    ${
                        pedido.statusPedido === "PENDENTE"
                        ? "⚠️"
                        : ""
                    }
                </td>
            </tr>
        `;
    });
}

document
.getElementById("pedidoForm")
.addEventListener("submit", async e => {

    e.preventDefault();

    const pedido = {
        ordemProducao: ordemProducao.value,
        tipoPedido: tipoPedido.value,
        corTampa: corTampa.value
    };

    await fetch("/api/pedidos",{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:JSON.stringify(pedido)
    });

    const toast =
        new bootstrap.Toast(
            document.getElementById("toastSucesso")
        );

    toast.show();
});

carregarPedidos();