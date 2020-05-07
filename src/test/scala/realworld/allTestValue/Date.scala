package realworld.allTestValue
import java.sql.Timestamp
import java.util.Date
object Date {
def currentWhenInserting=new Timestamp((new Date).getTime)
}
