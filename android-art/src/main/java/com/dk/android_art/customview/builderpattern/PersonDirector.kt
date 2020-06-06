package com.dk.android_art.customview.builderpattern

/**
 * @create on 2020/6/1 23:17
 * @description 指挥者，用它来控制建造过程，也用它来隔离用户与构造过程的关联
 * [pb] 用户告诉指挥者，我需要什么样的人
 * @author mrdonkey
 */
class PersonDirector(private val pb: PersonBuilder) {

    /**
     * 根据用户的选择建造小人
     */
    fun createPerson() {
        pb.buildHead()
        pb.buildBody()
        pb.buildArmLeft()
        pb.buildArmRight()
        pb.buildLegLeft()
        pb.buildLegRight()
    }
}