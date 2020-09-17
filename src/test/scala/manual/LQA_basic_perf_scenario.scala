package manual

import io.gatling.core.Predef._
import io.gatling.http.Predef.{http, _}
import scala.concurrent.duration._

class LQA_basic_perf_scenario extends Simulation {

  val httpProtocol = http
    .baseUrl("https://reqres.in/api")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("python-requests/2.21.0")
    .connectionHeader("keep-alive")

  val headers_1 = Map(
    "Content-type" -> "application/json")

  // Defining user ids for test and pickup random one
  val user_id = csv("user_ids.csv").random

  // Defining salary for test and pickup random one
  val job = csv("jobs.csv").random

  val scn = scenario("LQA Perf scenario")
    //applying values into scenario
    .feed(user_id)
    .feed(job)

    //execute request
    .exec(http("Get Employee data")
      .get("/users/${uid}")
      .headers(headers_1)
      .check(
        status.in(200, 404),
        status.transform(status => 200.equals(status)).saveAs("statusOk")
      ))

    //pause
    .pause(1,5)

    //condition
    .doIf("${statusOk}") {
      exec(http("Update salary")
        .put("/users/${uid}")
        .headers(headers_1)
				.body(StringBody(
					"""
					 |{
					 |		    "name": "${uid}",
           |    "job": "${job_id}"
					 |}
          """.stripMargin))
				.check(status.is(200)))
    }

  setUp(
    scn.inject(
      nothingFor(4 seconds), // 1
      atOnceUsers(10), // 2
      rampUsers(10) during (5 seconds), // 3
      constantUsersPerSec(20) during (15 seconds), // 4
      constantUsersPerSec(20) during (15 seconds) randomized, // 5
      rampUsersPerSec(10) to 20 during (1 minutes), // 6
      rampUsersPerSec(10) to 20 during (1 minutes) randomized, // 7
      heavisideUsers(1000) during (20 seconds) // 8
    ).protocols(httpProtocol)
  )
}