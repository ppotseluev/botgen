package botgen.dao.impl

import java.util.concurrent.ConcurrentHashMap

import botgen.dao.KeyValueDao
import cats.Id

class InMemoryKeyValueDao[K, V]
  extends KeyValueDao[Id, K, V] {

  private val map = new ConcurrentHashMap[K, V]()

  override def put(key: K, value: V): Unit =
    map.put(key, value)

  override def get(key: K): Option[V] =
    Option(map.get(key))
}
