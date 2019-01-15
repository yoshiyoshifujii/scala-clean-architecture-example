package entities

case class ClientName(value: String) {
  assert(value.length <= 50, "client name fields maximum length from 50 characters")
}
