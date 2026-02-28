package com.sohitechnology.gymstudio.hammer.data.cache

import com.sohitechnology.gymstudio.hammer.data.model.MemberDto

object MemberCache {
    var members: List<MemberDto>? = null // memory cache
    fun clear() {
        members = null
    }
}
