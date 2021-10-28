package ie.wit.tennisapp.models

import timber.log.Timber.i

class ResultsMemStore : ResultStore {

    var lastId = 0L

    private fun getId(): Long {
        return lastId++
    }

    private val results = ArrayList<ResultModel>()

    override fun findAll(): List<ResultModel> {
        return results
    }

    override fun create(result: ResultModel) {
        result.id = getId()
        results.add(result)
        logAll()
    }

    override fun update(result: ResultModel) {
        var foundResult: ResultModel? = results.find { p -> p.id == result.id }
        println(foundResult)
        if (foundResult != null) {
            foundResult.playerOne = result.playerOne
            foundResult.playerTwo = result.playerTwo
            foundResult.result = result.result
            logAll()
        }
    }

    private fun logAll() {
        results.forEach{ i("$it") }
    }
}