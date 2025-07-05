package com.example.nookstyle.util

import android.content.Context
import android.content.SharedPreferences
import com.example.nookstyle.model.ReportData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

object ReportManager {
    private const val PREF_NAME = "report_data"
    private const val KEY_REPORTS = "reports"
    private const val KEY_REPORT_COUNT = "report_count_"
    
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * 신고 데이터 저장
     */
    fun saveReport(reportData: ReportData) {
        val reports = getReports().toMutableList()
        reports.add(reportData)
        
        val json = gson.toJson(reports)
        sharedPreferences.edit().putString(KEY_REPORTS, json).apply()
        
        // 이미지별 신고 횟수 증가
        val reportCount = getReportCount(reportData.imageName) + 1
        sharedPreferences.edit().putInt(KEY_REPORT_COUNT + reportData.imageName, reportCount).apply()
    }
    
    /**
     * 모든 신고 데이터 가져오기
     */
    fun getReports(): List<ReportData> {
        val json = sharedPreferences.getString(KEY_REPORTS, "[]")
        val type = object : TypeToken<List<ReportData>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    /**
     * 특정 이미지의 신고 횟수 가져오기
     */
    fun getReportCount(imageName: String): Int {
        return sharedPreferences.getInt(KEY_REPORT_COUNT + imageName, 0)
    }
    
    /**
     * 특정 이미지가 이미 신고되었는지 확인
     */
    fun isReported(imageName: String): Boolean {
        return getReportCount(imageName) > 0
    }
    
    /**
     * 신고 데이터 삭제 (이미지 삭제 시)
     */
    fun removeReports(imageName: String) {
        val reports = getReports().filter { it.imageName != imageName }
        val json = gson.toJson(reports)
        sharedPreferences.edit().putString(KEY_REPORTS, json).apply()
        
        // 신고 횟수도 삭제
        sharedPreferences.edit().remove(KEY_REPORT_COUNT + imageName).apply()
    }
    
    /**
     * 신고 처리 완료 표시
     */
    fun markAsProcessed(imageName: String) {
        val reports = getReports().map { report ->
            if (report.imageName == imageName) {
                report.copy(isProcessed = true)
            } else {
                report
            }
        }
        val json = gson.toJson(reports)
        sharedPreferences.edit().putString(KEY_REPORTS, json).apply()
    }
} 