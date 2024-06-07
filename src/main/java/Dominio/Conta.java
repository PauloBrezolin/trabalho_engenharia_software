package Dominio;

import java.io.Serializable;

import javax.persistence.*;

@Entity
public class Conta implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    private int numero;

    private int digito;

    private double saldo;

    private int tipo_conta;

    public Conta() {
    }

    public Conta( Pessoa pessoa, int numero,  int digito, double saldo, int tipo_conta) {
        this.pessoa = pessoa;
        this.numero = numero;
        this.digito = digito;
        this.saldo = saldo;
        this.tipo_conta = tipo_conta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public int getNumero() {
        return numero;
    }

    public int getDigito() {
        return digito;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setDigito(int digito) {
        this.digito = digito;
    }

    public int getTipo_conta() {
        return tipo_conta;
    }

    public void setTipo_conta(int tipo_conta) {
        this.tipo_conta = tipo_conta;
    }

    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", pessoa=" + pessoa +
                ", numero=" + numero +
                ", digito=" + digito +
                ", saldo=" + saldo +
                ", tipo_conta=" + tipo_conta +
                '}';
    }
}