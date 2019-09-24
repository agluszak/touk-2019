package io.gitlab.agluszak.tickets.services

object NameVerification {
  //TODO config
  val minNameLength = 3
  val surnameSeparator = "-"

  def verifyName(name: String): Boolean = {
    name.length >= minNameLength && name.charAt(0).isUpper && name.forall(_.isLetter)
  }

  def verifySurname(surname: String): Boolean = {
    val parts = surname.split("-")
    parts.length match {
      case 1 => verifyName(parts(0))
      case 2 => verifyName(parts(0)) && verifyName(parts(1))
      case _ => false
    }
  }
}
