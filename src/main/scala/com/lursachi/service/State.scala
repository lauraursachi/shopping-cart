package com.lursachi.service


trait State {

  def addOrReplaceExisting(currentState: Map[Int, Int], key: Int, newValue: Int) = {
    def addNew() = currentState + (key -> newValue)

    def replaceExistingValue() = {
      val stateWithoutEntry = currentState filterNot { case (k, _) => k == key }
      stateWithoutEntry + (key -> newValue)
    }

    currentState.get(key) match {
      case Some(_) => replaceExistingValue()
      case None => addNew()
    }
  }

  def addToExisting(currentState: Map[Int, Int], key: Int, valueToAdd: Int) = {
    currentState.get(key) match {
      case Some(existing) =>
        val stateWithoutEntry = currentState filterNot { case (k, _) => k == key }
        stateWithoutEntry + (key -> (existing + valueToAdd))
      case None => currentState
    }
  }

  def removeEntry(currentState: Map[Int, Int], key: Int) = currentState filterNot { case (k, _) => k == key }


}
