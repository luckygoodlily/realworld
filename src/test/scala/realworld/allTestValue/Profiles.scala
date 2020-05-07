package realworld.allTestValue


import model.profile.model.output.{Profile, control}
import model.profile.model.output.control.ControlProfile

object Profiles {
  val profileResult = Some(control.ControlProfile(
    Profile("lily",None,None,true)))

  val profileResult1 = Some(ControlProfile(
    Profile("lily",None,None,false)))
  val profiletest = Profile("lily",None,None,false)

}
