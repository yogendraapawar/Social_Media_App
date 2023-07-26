package com.example.myapplication

import kotlin.properties.Delegates

private var isUserRegistered by Delegates.notNull<Boolean>()
