package com.example.reactivewebcrawlerservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling

class ReactiveWebCrawlerServiceApplication

fun main(args: Array<String>) {
	runApplication<ReactiveWebCrawlerServiceApplication>(*args)
}
