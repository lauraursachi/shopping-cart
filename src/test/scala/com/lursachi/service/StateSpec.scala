package com.lursachi.service

import org.scalatest.{Matchers, WordSpecLike}


class StateSpec extends WordSpecLike
  with Matchers {

  "An object with State" when {
    val MyStatefulObject = new State(){}

    "calling addOrReplaceExisting for a new id " should {
      "add it to current state" in {
        val newState = MyStatefulObject.addOrReplaceExisting(Map.empty, 1, 100)

        newState should contain(1 -> 100)
      }
    }

    "calling addOrReplaceExisting for an existing id " should {
      "replace it state" in {
        val newState = MyStatefulObject.addOrReplaceExisting(Map(1 -> 20, 2 -> 40), 1, 100)

        newState should contain(1 -> 100)
        newState should contain(2 -> 40)
      }
    }

    "calling addToExisting for an existing id " should {
      "add to current state" in {
        val newState = MyStatefulObject.addToExisting(Map(1 -> 20, 2 -> 40), 1, 100)

        newState should contain(1 -> 120)
        newState should contain(2 -> 40)
      }
    }

    "calling removeEntry for an existing id " should {
      "remove it from current state" in {
        val newState = MyStatefulObject.removeEntry(Map(1 -> 20, 2 -> 40), 1)

        newState shouldNot contain(1 -> 20)
        newState should contain(2 -> 40)
      }
    }

    "calling removeEntry for an inexisting id " should {
      "not affect current state" in {
        val newState = MyStatefulObject.removeEntry(Map(1 -> 20, 2 -> 40), 3)

        newState should contain(2 -> 40)
        newState should contain(1 -> 20)
      }
    }

  }
}
