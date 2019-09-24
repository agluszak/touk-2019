package unit

import org.scalatest.{MustMatchers, WordSpec}

class NameVerificationTest extends WordSpec with MustMatchers {

  import io.gitlab.agluszak.tickets.services.NameVerification._

  "NameVerification" should {
    "return true for correct names" in {
      verifyName("Andrzej") mustBe true
      verifyName("Łukasz") mustBe true
    }
    "return false for incorrect names" in {
      verifyName("Al") mustBe false
      verifyName("zdzisiek") mustBe false
    }
    "return true for correct surnames" in {
      verifySurname("Głuszak") mustBe true
      verifySurname("Kowalska-Goździńska") mustBe true
    }
    "return false for incorrect surnames" in {
      verifySurname("A-B-C") mustBe false
      verifySurname("hej-ho") mustBe false
      verifySurname("super-Toster") mustBe false
      verifySurname("Jak to leciało?") mustBe false
    }
  }
}
