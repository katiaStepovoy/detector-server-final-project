package com.example.reactivealertsmanagementservice

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = ["spring.mongodb.embedded.version=3.5.5"])
class ReactiveAlertsManagementServiceApplicationTests {

	@Autowired
	lateinit var webTestClient: WebTestClient

	@Autowired
	lateinit var alertService: AlertService

	@BeforeEach
	fun setup() {
		alertService.cleanup().block()
	}

	@Test
	fun `test search alerts`() {
		// Create some alerts to search for
		/* val alert1 = AlertBoundary()
		alert1.alertId = null
		alert1.location = "Location 1"
		alert1.website = "Website 1"
		alert1.timestamp = Date()
		alert1.publisher = "test search alerts()"
		alert1.content = "Test alert 1"

		val alert2 = AlertBoundary()
		alert2.alertId = null
		alert2.location = "Location 2"
		alert2.website = "Website 2"
		alert2.timestamp = Date()
		alert2.publisher = "test search alerts()"
		alert2.content = "Test alert 2"

		alertService.create(alert1).block()
		alertService.create(alert2).block()

		// Search for alerts by location
		val searchLocation = "Location 1"
		val response = webTestClient.get()
			.uri("/alerts?filters=byLocation:$searchLocation")
			.exchange()
			.expectStatus().isOk
			.expectBodyList(AlertBoundary::class.java)
			.returnResult()

		val alerts = response.responseBody
		assertNotNull(alerts)
		assertEquals(1, alerts!!.size)
		assertEquals(alert1.location, alerts[0].location)

		// Search for alerts by website
		val searchWebsite = "Website 2"
		val response2 = webTestClient.get()
			.uri("/alerts?filters=byWebsite:$searchWebsite")
			.exchange()
			.expectStatus().isOk
			.expectBodyList(AlertBoundary::class.java)
			.returnResult()

		val alerts2 = response2.responseBody
		assertNotNull(alerts2)
		assertEquals(1, alerts2!!.size)
		assertEquals(alert2.website, alerts2[0].website)*/
	}




}
