package com.sohitechnology.clubmanagement.data.cache

import com.sohitechnology.clubmanagement.data.model.MemberDto

object MemberCache {
    var members: List<MemberDto>? = null // memory cache
    fun clear() {
        members = null
    }
}
