package io.gitlab.agluszak.tickets.http.responses

case class ApiError[T](errorType: String, description: String, param: Option[T])