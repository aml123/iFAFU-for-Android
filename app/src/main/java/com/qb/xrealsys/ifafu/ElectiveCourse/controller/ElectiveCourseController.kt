package com.qb.xrealsys.ifafu.ElectiveCourse.controller

import com.qb.xrealsys.ifafu.Base.controller.AsyncController
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.UpdateElectiveCourseListViewDelegate
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourseList
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveFilter
import com.qb.xrealsys.ifafu.ElectiveCourse.web.ElectiveCourseInterface
import com.qb.xrealsys.ifafu.Tool.ConfigHelper
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import com.qb.xrealsys.ifafu.User.model.User

class ElectiveCourseController (
        userController: UserAsyncController,
        configHelper: ConfigHelper): AsyncController(userController.threadPool){

    private val electiveCourseList: ElectiveCourseList = ElectiveCourseList()

    private val electiveCourseInterface: ElectiveCourseInterface = ElectiveCourseInterface(
            configHelper.GetSystemValue("host"),
            userController)

    private val userController: UserAsyncController = userController

    var updateDelegate: UpdateElectiveCourseListViewDelegate? = null

    fun getIndex() {
        this.threadPool.execute {
            val user: User = this.userController.data
            val response: Response = this.electiveCourseInterface.getElectiveCourseIndex(
                    user.account,
                    user.name,
                    this.electiveCourseList)
            if (response.isSuccess) {
                if (this.updateDelegate != null) {
                    updateDelegate!!.updateElectiveCourseList(electiveCourseList.courses)
                }
            } else {
                updateDelegate!!.errorElectiveCourseList(response.message)
            }
        }
    }

    fun getFilter(): ElectiveFilter {
        return this.electiveCourseList.filter
    }

    fun searchByName(courseName: String) {
        this.threadPool.execute {
            val user: User = this.userController.data
            val response: Response = this.electiveCourseInterface.searchElectiveCourseByName(
                    user.account,
                    user.name,
                    this.electiveCourseList,
                    courseName)
            if (response.isSuccess) {
                if (this.updateDelegate != null) {
                    updateDelegate!!.updateElectiveCourseList(electiveCourseList.courses)
                }
            } else {
                updateDelegate!!.errorElectiveCourseList(response.message)
            }
        }
    }
}