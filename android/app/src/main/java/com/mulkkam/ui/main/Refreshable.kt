package com.mulkkam.ui.main

/**
 * Fragment 또는 View가 다시 선택되었을 때 새로고침 동작을 수행하도록 하는 인터페이스입니다.
 *
 * 이 인터페이스를 구현하면, 해당 객체가 다시 선택될 경우 [onReselected] 메서드가 호출됩니다.
 * 일반적으로 하단 탭(Tab)에서 현재 선택된 탭을 다시 눌렀을 때 사용됩니다.
 */
interface Refreshable {
    fun onReselected() = Unit
}
