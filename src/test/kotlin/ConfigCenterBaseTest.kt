import kotlin.test.Test
import kotlin.test.assertEquals

internal class ConfigCenterBaseTest {
    class TestOption : ConfigCenterBase() {
        var test1 by obj("test1") { "testString" }
        var list by obj("list") { listOf("l1", "l2") }
        var str by obj<String>("str")
    }

    @Test
    fun test() {
        val json = """
            {
              "test1" : "testString",
              "list" : [ "l1", "l2" ],
              "str" : null
            }
        """.trimIndent().decodeJson()
        val testOption = TestOption()
        testOption.setValues(json.encode())
        assertEquals(json, testOption.getValues().decodeJson())
    }
}