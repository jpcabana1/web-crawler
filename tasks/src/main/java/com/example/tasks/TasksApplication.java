package com.example.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TasksApplication {

	public static void main(String[] args) {
		SpringApplication.run(TasksApplication.class, args);
	}
}

// TODO: WebCrawler Demo
/*
 1 - Criar uma lista de urls para fazer busca (6 - 12 urls)
 2 - Ler conteúdo e salvar Url, Descrição, Título e Documento.
 3 - Criar uma thread pool para executar ETL de 3 urls paralelamente
 */