package com.example.hamburgueriaz;

import android.content.Intent; // NOVO IMPORT
import android.net.Uri;       // NOVO IMPORT
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;     // NOVO IMPORT (ou descomentar se já existia)

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    // Variáveis para os elementos da interface (Views)
    EditText editTextNomeCliente;
    CheckBox checkBoxBacon, checkBoxQueijo, checkBoxOnionRings;
    Button buttonSubtrair, buttonAdicionar, buttonEnviarPedido;
    TextView textViewQuantidade, textViewPrecoTotal;

    // Variáveis para os dados do pedido
    int quantidade = 1;
    final double PRECO_BASE_HAMBURGUER = 20.00;
    final double PRECO_BACON = 2.00;
    final double PRECO_QUEIJO = 2.00;
    final double PRECO_ONION_RINGS = 3.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar as Views
        editTextNomeCliente = findViewById(R.id.editTextNomeCliente);
        checkBoxBacon = findViewById(R.id.checkBoxBacon);
        checkBoxQueijo = findViewById(R.id.checkBoxQueijo);
        checkBoxOnionRings = findViewById(R.id.checkBoxOnionRings);
        buttonSubtrair = findViewById(R.id.buttonSubtrair);
        buttonAdicionar = findViewById(R.id.buttonAdicionar);
        buttonEnviarPedido = findViewById(R.id.buttonEnviarPedido);
        textViewQuantidade = findViewById(R.id.textViewQuantidade);
        textViewPrecoTotal = findViewById(R.id.textViewPrecoTotal);

        // Atualizar a interface inicial (quantidade e preço)
        atualizarInterfaceQuantidade();
        // Nota: Ao iniciar, calcularEAtualizarPrecoTotal() é chamado, o que define
        // o textViewPrecoTotal com o preço. O resumo completo só aparece após clicar em "FAZER PEDIDO".
        // Se quiser que o resumo apareça ao iniciar, teria que ajustar essa lógica.
        calcularEAtualizarPrecoTotal();


        // Configurar os listeners dos botões de quantidade
        buttonAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantidade++;
                atualizarInterfaceQuantidade();
                calcularEAtualizarPrecoTotal();
            }
        });

        buttonSubtrair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantidade > 1) {
                    quantidade--;
                    atualizarInterfaceQuantidade();
                    calcularEAtualizarPrecoTotal();
                }
            }
        });

        // Listeners para os CheckBoxes
        View.OnClickListener checkBoxListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularEAtualizarPrecoTotal();
            }
        };
        checkBoxBacon.setOnClickListener(checkBoxListener);
        checkBoxQueijo.setOnClickListener(checkBoxListener);
        checkBoxOnionRings.setOnClickListener(checkBoxListener);

        // Configurar o listener do botão Enviar Pedido
        buttonEnviarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processarPedido();
            }
        });
    }

    private void atualizarInterfaceQuantidade() {
        if (textViewQuantidade != null) {
            textViewQuantidade.setText(String.valueOf(quantidade));
        }
    }

    private void calcularEAtualizarPrecoTotal() {
        if (checkBoxBacon == null || checkBoxQueijo == null || checkBoxOnionRings == null || textViewPrecoTotal == null) {
            return;
        }

        double precoAdicionais = 0;
        if (checkBoxBacon.isChecked()) {
            precoAdicionais += PRECO_BACON;
        }
        if (checkBoxQueijo.isChecked()) {
            precoAdicionais += PRECO_QUEIJO;
        }
        if (checkBoxOnionRings.isChecked()) {
            precoAdicionais += PRECO_ONION_RINGS;
        }

        double precoTotalPedido = (PRECO_BASE_HAMBURGUER + precoAdicionais) * quantidade;

        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance();
        // Este método agora só atualiza o preço, não o resumo completo.
        // O resumo completo será montado em processarPedido e enviado por e-mail.
        // E também exibido no textViewPrecoTotal após clicar em "FAZER PEDIDO".
        textViewPrecoTotal.setText(formatadorMoeda.format(precoTotalPedido));
    }

    private void processarPedido() {
        String nomeCliente = "Cliente";
        if (editTextNomeCliente != null && editTextNomeCliente.getText() != null) {
            String nomeDigitado = editTextNomeCliente.getText().toString().trim();
            if (!nomeDigitado.isEmpty()) {
                nomeCliente = nomeDigitado;
            }
        }

        boolean temBacon = checkBoxBacon != null && checkBoxBacon.isChecked();
        boolean temQueijo = checkBoxQueijo != null && checkBoxQueijo.isChecked();
        boolean temOnionRings = checkBoxOnionRings != null && checkBoxOnionRings.isChecked();

        double precoAdicionais = 0;
        if (temBacon) precoAdicionais += PRECO_BACON;
        if (temQueijo) precoAdicionais += PRECO_QUEIJO;
        if (temOnionRings) precoAdicionais += PRECO_ONION_RINGS;
        double precoTotalFinal = (PRECO_BASE_HAMBURGUER + precoAdicionais) * quantidade;

        StringBuilder resumoPedido = new StringBuilder();
        resumoPedido.append("Nome do cliente: ").append(nomeCliente).append("\n");
        resumoPedido.append("Tem Bacon? ").append(temBacon ? "Sim" : "Não").append("\n");
        resumoPedido.append("Tem Queijo? ").append(temQueijo ? "Sim" : "Não").append("\n");
        resumoPedido.append("Tem Onion Rings? ").append(temOnionRings ? "Sim" : "Não").append("\n");
        resumoPedido.append("Quantidade: ").append(quantidade).append("\n");
        resumoPedido.append("Preço final: ").append(NumberFormat.getCurrencyInstance().format(precoTotalFinal));

//         Opcional: Exibir o resumo na tela antes de enviar o e-mail
         if (textViewPrecoTotal != null) {
            textViewPrecoTotal.setText(resumoPedido.toString());
         }

        // Enviar o pedido por e-mail
        String assuntoEmail = "Pedido de " + nomeCliente;
        enviarPedidoPorEmail(assuntoEmail, resumoPedido.toString()); // CHAMADA DESCOMENTADA E AJUSTADA
    }

    // --- MÉTODO PARA ENVIAR E-MAIL ADICIONADO ---
    private void enviarPedidoPorEmail(String assunto, String corpoDoEmail) {
        Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
        intentEmail.setData(Uri.parse("mailto:")); // Apenas apps de e-mail devem responder
        // Opcional: Adicionar um endereço de e-mail de destino fixo
         intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"danrleybrasil.s@gmail.com"});
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, assunto);
        intentEmail.putExtra(Intent.EXTRA_TEXT, corpoDoEmail);

        // Verificar se existe um app de e-mail para lidar com o Intent
        if (intentEmail.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intentEmail, "Escolha um app de e-mail:"));
        } else {
            Toast.makeText(this, "Nenhum aplicativo de e-mail encontrado.", Toast.LENGTH_LONG).show();
        }
    }
    // --- FIM DO MÉTODO DE E-MAIL ---
}