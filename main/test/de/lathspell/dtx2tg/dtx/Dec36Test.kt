package DTX

import org.junit.Test
import kotlin.test.assertEquals

class Dec36Test {
    [Test]
    fun testDecimal36() {
        assertEquals(15, Dec36().fromDec36("0F"))
        assertEquals(35, Dec36().fromDec36("0Z"))
        assertEquals(36, Dec36().fromDec36("10"))
        assertEquals(36*36-1, Dec36().fromDec36("ZZ"))

        assertEquals("0F", Dec36().toDec36(15))
        assertEquals("0Z", Dec36().toDec36(35))
        assertEquals("10", Dec36().toDec36(36))
        assertEquals("ZZ", Dec36().toDec36(36*36-1))
    }
}
