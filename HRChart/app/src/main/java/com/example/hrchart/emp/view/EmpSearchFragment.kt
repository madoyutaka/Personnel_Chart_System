package com.example.hrchart.emp.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.hrchart.R
import com.example.hrchart.databinding.FragmentEmpSearchBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 従業員検索画面 Fragment
 * 画面ID:
 *
 * @author K.Takahashi
 * created on 2023/10/12
 */
class EmpSearchFragment : BottomSheetDialogFragment() {

    companion object {
        /** TAG */
        private const val TAG = "EmpSearchFragment"
    }

    /** viewModel */
    private val viewModel: EmpSearchViewModel by activityViewModels()
    /** binding */
    private lateinit var binding: FragmentEmpSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView Start")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_emp_search, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        // クリアボタン
        binding.empSearchClearButton.setOnClickListener {
            binding.empSearchNameEt.text.clear()
            binding.empSearchStatusDropdownItem.text.clear()
            binding.empSearchAreaDropdownItem.text.clear()
            binding.empSearchJobDropdownItem.text.clear()

            binding.empSearchNameEt.clearFocus()
        }

        // キャンセルボタン
        binding.empSearchCloseButton.setOnClickListener {
            dismiss()
        }

        // 検索ボタン
        binding.empSearchButton.setOnClickListener {

            val name = binding.empSearchNameEt.text.toString()
            val status = binding.empSearchStatusDropdownItem.text.toString()
            val area = binding.empSearchAreaDropdownItem.text.toString()
            val job = binding.empSearchJobDropdownItem.text.toString()

            val searchArray = arrayOf(name, status, area, job)
            // 検索結果をViewModelへ配列で渡す
            viewModel.onClickSearch(searchArray)
            dismiss()
        }

        val etName = binding.empSearchNameEt
        // キーボード制御
        keyBoardCtrl(etName)

        Log.d(TAG, "onCreateView End")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated Start")
        this.isCancelable = false
        setDialogMatchParent()
        Log.d(TAG, "onViewCreated End")
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        Log.d(TAG, "onCreateDialog Start")
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(bottomSheet).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                    isHideable = true
                }
            }
        }
        Log.d(TAG, "onCreateDialog End")
        return bottomSheetDialog
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Start")
        // ドロップダウン初期化
        val statusDropdown : AutoCompleteTextView = binding.empSearchStatusDropdownItem
        val areasDropdown : AutoCompleteTextView = binding.empSearchAreaDropdownItem
        val jobsDropdown : AutoCompleteTextView = binding.empSearchJobDropdownItem
        initDropdown(statusDropdown, areasDropdown, jobsDropdown)
        Log.d(TAG, "onResume End")
    }

    /**
     * ダイアログのMatchParentの設定
     */
    private fun setDialogMatchParent() {
        dialog?.window?.let {
            Log.d(TAG, "setDialogMatchParent")
            val params = it.attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            it.attributes = params
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    /**
     * initDropdown
     * ドロップダウン初期化メソッド
     * @param statusDropdown ステータス
     * @param areasDropdown エリア
     * @param jobsDropdown 職種
     */
    private fun initDropdown(statusDropdown: AutoCompleteTextView, areasDropdown: AutoCompleteTextView, jobsDropdown: AutoCompleteTextView) {
        Log.d(TAG, "initDropdown")
        // テストデータ
        val statusArray = arrayOf("すべて","在職", "退職", "休職", "辞退", "内定", "見込み")
        val areasArray = arrayOf("すべて","東京", "大阪", "愛知", "宮城", "福岡")
        val jobsArray = arrayOf("すべて","開発エンジニア", "営業", "管理", "ネットワーク")

        val statusArrayAdapter = ArrayAdapter(statusDropdown.rootView.context, R.layout.item_drop_down, statusArray)
        statusDropdown.setAdapter(statusArrayAdapter)
        val areasArrayAdapter = ArrayAdapter(areasDropdown.rootView.context, R.layout.item_drop_down, areasArray)
        areasDropdown.setAdapter(areasArrayAdapter)
        val jobsArrayAdapter = ArrayAdapter(jobsDropdown.rootView.context, R.layout.item_drop_down, jobsArray)
        jobsDropdown.setAdapter(jobsArrayAdapter)
    }

    /**
     * hideIME
     * ソフトキーボードを非表示にする
     * @param et EditText
     */
    private fun hideIME(et: EditText) {
        Log.d(TAG, "hideIME")
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(et.windowToken, 0)
    }

    /**
     * keyBoardCtrl
     * ソフトキーボードの処理(EditText1つのみ)
     * @param et EditText
     */
    private fun keyBoardCtrl(et: EditText) {
        Log.d(TAG, "keyBoardCtrl")
        // ソフトキーボードのDone押下時処理
        et.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // フォーカスアウト
                binding.empSearchNameEt.clearFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        // フォーカスアウト処理
        et.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideIME(et)
            }
        }
        // 背景をタップしたらソフトキーボードを閉じる
        binding.root.setOnClickListener {
            hideIME(et)
        }
    }

}