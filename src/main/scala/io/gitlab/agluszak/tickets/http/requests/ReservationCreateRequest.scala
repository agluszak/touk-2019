package io.gitlab.agluszak.tickets.http.requests

import io.gitlab.agluszak.tickets.{ScreeningId, TicketsQuantity}

case class ReservationCreateRequest(screeningId: ScreeningId, name: String,
                                    surname: String, seats: List[Int], tickets: List[TicketsQuantity])