package Iniciar;

import EntityFactory.EntityFactory;
import javax.persistence.EntityManager;

import Dominio.Conta;
import Dominio.Pessoa;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static String cpf;

    public static void setCpf(String cpf) {
        Main.cpf = cpf;
    }

    public static void main(String[] args) {

        boolean login_sucesso = false;
        String cpf = solicitarCPF();
        setCpf(cpf);

        while (!login_sucesso)
        if (LoginUsuario()) {
            if (LoginConta()) {
                login_sucesso = true;
                int conta = EscolherConta();
                Transacoes(conta);
            } else {
                CadastroConta();
            }
        } else {
            CadastroUsuario();
        }
    }

    private static String solicitarCPF() {

        Scanner scanner = new Scanner(System.in);
        String cpf;

        do {
            System.out.println("Digite o seu CPF (SÓ NÚMEROS):");
            cpf = scanner.nextLine();
            if (!cpf.matches("\\d{11}")) {
                System.out.println("CPF inválido! O CPF deve conter exatamente 11 dígitos numéricos.");
            }
        } while (!cpf.matches("\\d{11}"));

        return cpf;
    }

    public static void CadastroUsuario() {

        Scanner scanner = new Scanner(System.in);
        EntityManager em = EntityFactory.getEntityFactory();

        String nome;
        String telefone;
        System.out.println("Você ainda não está cadastrado!");
        System.out.println("Realize seu cadastro abaixo.");

        do {
            System.out.println("Digite o seu nome:");
            nome = scanner.nextLine();
            if (!nome.matches("^[a-zA-Z\\s]+$")) {
                System.out.println("Nome inválido! O nome deve conter apenas letras.");
            }
        } while (!nome.matches("^[a-zA-Z\\s]+$"));

        do {
            System.out.println("Digite o seu telefone: (COM DDD E SÓ NÚMEROS)");
            telefone = scanner.nextLine();
            if (!telefone.matches("\\d{11}")) {
                System.out.println("Telefone inválido! O telefone deve conter exatamente 11 dígitos numéricos.");
            }
        } while (!telefone.matches("\\d{11}"));

        EntityFactory.cadastroUsuario(nome, telefone, cpf, em);
    }

    public static boolean LoginUsuario() {

        EntityManager em = EntityFactory.getEntityFactory();

        boolean validacaoCPF = false;

        System.out.println("Seja bem vindo ao Banco do Paulo!");
        validacaoCPF = EntityFactory.validarCPF(cpf, em);
        return validacaoCPF;
    }

    public static void CadastroConta() {

        Scanner scanner = new Scanner(System.in);
        Random gerador = new Random();
        EntityManager em = EntityFactory.getEntityFactory();

        int digito = gerador.nextInt(10);
        int numero;
        long pessoa_id;
        Pessoa pessoa;
        double saldo = 0;
        int tipo_conta;
        System.out.println("Vamos criar sua conta!");

        pessoa = EntityFactory.getPessoa(cpf, em);
        pessoa_id = EntityFactory.getID(cpf, em);

        do {
            System.out.println("Escolha o tipo da sua conta:");
            System.out.println("1- CORRENTE.");
            System.out.println("2- POUPANÇA.");
            tipo_conta = scanner.nextInt();
            if (tipo_conta != 1 && tipo_conta != 2) {
                System.out.println("Tipo de conta inválido! Você deve digitar 1 ou 2.");
            }
        } while ((tipo_conta != 1 && tipo_conta != 2) || (!EntityFactory.validaTipoConta(pessoa_id, tipo_conta, em)));

        do {
            numero = gerador.nextInt(10000000);
        } while (!EntityFactory.validaNumeroConta(numero, em));
        EntityFactory.cadastroConta(pessoa, numero, digito, saldo, tipo_conta, em);
    }

    public static boolean LoginConta() {

        EntityManager em = EntityFactory.getEntityFactory();

        long pessoa_id = EntityFactory.getID(cpf, em);
        boolean validacaoConta = EntityFactory.validarConta(pessoa_id, em);
        return validacaoConta;
    }

    public static int EscolherConta() {

        int numero = 0;
        int escolha;

        Scanner scanner = new Scanner(System.in);
        EntityManager em = EntityFactory.getEntityFactory();
        long pessoa_id = EntityFactory.getID(cpf, em);
        int numeroDeContas = EntityFactory.contarContas(pessoa_id, em);
        int tipo_conta = EntityFactory.getTipoConta(pessoa_id, em);

        if (numeroDeContas == 1) {
            if (tipo_conta == 1) {
                do {
                    System.out.println("Você já tem uma conta corrente, deseja criar uma conta poupança?");
                    System.out.println("1- SIM, CRIAR CONTA POUPANÇA.");
                    System.out.println("2- NÃO, USAR CONTA CORRENTE.");
                    escolha = scanner.nextInt();
                    if (escolha != 1 && escolha != 2) {
                        System.out.println("Opção inválida! Você deve digitar 1 ou 2.");
                    }
                } while (escolha != 1 && escolha != 2);

                if (escolha == 1) {
                    CadastroConta();
                    System.exit(1);
                }
            } else if (tipo_conta == 2) {
                do {
                    System.out.println("Você já tem uma conta poupança, deseja criar uma conta corrente?");
                    System.out.println("1- SIM, CRIAR CONTA CORRENTE.");
                    System.out.println("2- NÃO, USAR CONTA POUPANÇA.");
                    escolha = scanner.nextInt();
                    if (escolha != 1 && escolha != 2) {
                        System.out.println("Opção inválida! Você deve digitar 1 ou 2.");
                    }
                } while (escolha != 1 && escolha != 2);

                if (escolha == 1) {
                    CadastroConta();
                    System.exit(1);
                }
            }

            numero = EntityFactory.getNumeroConta(pessoa_id, em);
        } else if (numeroDeContas == 2) {
            do {
                System.out.println("Você tem duas contas, qual deseja usar?");
                System.out.println("1- CORRENTE.");
                System.out.println("2- POUPANÇA.");
                tipo_conta = scanner.nextInt();
                numero = EntityFactory.getNumeroContaByTipo(pessoa_id, tipo_conta, em);
                if (tipo_conta != 1 && tipo_conta != 2) {
                    System.out.println("Tipo de conta inválido! Você deve digitar 1 ou 2.");
                }
            } while (tipo_conta != 1 && tipo_conta != 2);
        }

        return numero;
    }

    public static void Transacoes(int conta) {

        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);
        EntityManager em = EntityFactory.getEntityFactory();

        int escolha;
        double valor;
        double saque;
        double deposito;
        long id_conta = EntityFactory.getIdContaByNumero(conta, em);
        
        do {
            double saldo = EntityFactory.consultarSaldo(conta, em);
            do {
                System.out.println("Bem vindo ao menu de transações!");
                System.out.println("Você está usando a conta " + conta + "!");

                System.out.println("1 - CONSULTAR SALDO.");
                System.out.println("2 - DEPOSITAR.");
                System.out.println("3 - SACAR.");
                System.out.println("4 - REALIZAR TRANSFERÊNCIA.");
                System.out.println("5 - SAIR.");
                escolha = scanner.nextInt();
                if (escolha != 1 && escolha != 2 && escolha != 3 && escolha != 4 && escolha != 5) {
                    System.out.println("Opção inválida! Você deve digitar 1, 2, 3 ou 4.");
                }
            } while (escolha != 1 && escolha != 2 && escolha != 3 && escolha != 4 && escolha != 5);

            switch (escolha) {
                case 1:
                    System.out.println("Seu saldo: R$" + saldo + "!");
                    break;
                case 2:
                    System.out.println("Digite o valor que deseja depositar: ");
                    deposito = scanner.nextDouble();
                    valor = saldo + deposito;
                    EntityFactory.atualizarSaldo(valor, id_conta, em);
                    System.out.println("Transação bem secedida!");
                    break;
                case 3:
                    System.out.println("Digite o valor que deseja sacar: ");
                    saque = scanner.nextDouble();
                    if (saque <= saldo) {
                    valor = saldo - saque;
                    EntityFactory.atualizarSaldo(valor, id_conta, em);
                    System.out.println("Transação bem secedida!");
                    }else if (saque > saldo) {
                        System.out.println("Saldo insuficiente! Saque um valor menor ou igual ao saldo.");
                        System.out.println("Seu saldo é: R$" + saldo + "!");
                    }
                    break;
                case 4:
                    int numero_conta_origem = conta;
                    System.out.println("Digite o número da conta que deseja transferir: ");
                    int numero_conta_destino = scanner.nextInt();

                    System.out.println("Digite o valor que deseja transferir: ");
                    valor = scanner.nextDouble();

                    double saldo_origem = EntityFactory.consultarSaldo(numero_conta_origem, em);
                    double saldo_destino = EntityFactory.consultarSaldo(numero_conta_destino, em);

                    Conta conta_origem = EntityFactory.getConta(numero_conta_origem, em);
                    Conta conta_destino = EntityFactory.getConta(numero_conta_destino, em);

                    long id_origem = EntityFactory.getIdContaByNumero(numero_conta_origem, em);
                    long id_destino = EntityFactory.getIdContaByNumero(numero_conta_destino, em);

                    LocalDate data = LocalDate.now();
                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String dataFormatada = data.format(formato);

                    if (valor <= saldo_origem) {
                        saldo_origem = saldo_origem - valor;
                        EntityFactory.atualizarSaldo(saldo_origem, id_origem, em);
                        saldo_destino = saldo_destino + valor;
                        EntityFactory.atualizarSaldo(saldo_destino, id_destino, em);
                        EntityFactory.registrarTrasferencia(conta_origem, conta_destino, valor, dataFormatada, em);
                    }else if (valor > saldo_origem) {
                        System.out.println("Saldo insuficiente! Saque um valor menor ou igual ao saldo.");
                        System.out.println("Seu saldo é: R$" + saldo_origem + "!");
                    }
                    break;
                case 5:
                    break;
            }
        }while(escolha != 5);
    }
}
