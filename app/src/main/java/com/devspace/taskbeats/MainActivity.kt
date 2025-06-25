package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.devspace.taskbeats.database.CategoryDao
import com.devspace.taskbeats.database.CategoryEntity
import com.devspace.taskbeats.database.TaskBeatDataBase
import com.devspace.taskbeats.database.TaskDao
import com.devspace.taskbeats.database.TaskEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var categories = listOf<CategoryUiData>()
    private var categoriesEntity = listOf<CategoryEntity>()
    private var taskEntity = listOf<TaskEntity>()
    private var tasks = listOf<TaskUiData>()
    private lateinit var rvCategory: RecyclerView
    private lateinit var ctnEmptyView: LinearLayout
    private lateinit var newTask: FloatingActionButton
    private lateinit var ivEmptyCategory: ImageView
    private lateinit var ivEmptyTasks: ImageView
    private lateinit var tvEmptyView: TextView
    private lateinit var tvEmptyViewSubtitle: TextView
    private lateinit var btnCreateEmpty: Button
    private var selectedCategoryName: String? = null




    private val categoryAdapter = CategoryListAdapter()
    private val taskAdapter by lazy {
        TaskListAdapter()
    }
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskBeatDataBase::class.java,
            "database-task-beat"
        ).build()
    }
    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()
    }
    private val taskDao: TaskDao by lazy {
        db.getTaskDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvCategory = findViewById(R.id.rv_categories)
        ctnEmptyView = findViewById(R.id.ll_empty_view)
        newTask = findViewById(R.id.fab)
        ivEmptyCategory = findViewById(R.id.iv_empty_category)
        ivEmptyTasks = findViewById(R.id.iv_empty_tasks)
        tvEmptyView = findViewById(R.id.tv_empty_view)
        tvEmptyViewSubtitle = findViewById(R.id.tv_empty_view_subtitle)
        btnCreateEmpty = findViewById(R.id.btn_create_empty)

        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)
        //val btnCreateEmpty = findViewById<Button>(R.id.btn_create_empty)

        btnCreateEmpty.setOnClickListener {
            if (selectedCategoryName == null) {
                showCreateCategoryBottonSheet()
            } else if (selectedCategoryName != "ALL") {
                showCreateUpdateTaskBottomSheetWithCategory(selectedCategoryName!!)
            } else {
                showCreateUpdateTaskBottomSheet()
            }
        }

        taskAdapter.setOnClickListenerTask { task ->
            showCreateUpdateTaskBottomSheet(task)
        }

        newTask.setOnClickListener {
            if (selectedCategoryName != null && selectedCategoryName != "ALL") {
                showCreateUpdateTaskBottomSheetWithCategory(selectedCategoryName!!)
            } else {
                showCreateUpdateTaskBottomSheet()
            }
        }

        categoryAdapter.setOnLongClickListener { categoryToBeDeleted ->
            if (categoryToBeDeleted.name != "+" && categoryToBeDeleted.name != "ALL") {
                lifecycleScope.launch(Dispatchers.IO) {
                    val taskCount = taskDao.getAllByCategoryName(categoryToBeDeleted.name).size
                    val title: String = this@MainActivity.getString(R.string.category_delete_title)
                    val description: String =
                        if (taskCount == 0) {
                            getString(R.string.category_no_tasks_description)
                        } else {
                            getString(R.string.category_with_tasks_description, taskCount)
                        }
                    val btnText: String = this@MainActivity.getString(R.string.delete)

                    withContext(Dispatchers.Main) {
                        showInfoDialog(
                            title,
                            description,
                            btnText,
                        ) {
                            val categoryEntityToBeDeleted = CategoryEntity(
                                categoryToBeDeleted.name,
                                categoryToBeDeleted.isSelected
                            )
                            deleteCategory(categoryEntityToBeDeleted)
                        }
                    }
                }
            }
        }

        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {
                showCreateCategoryBottonSheet()
            } else {
                val categoryTemp =
                    categories.map { item ->
                        when {
                            item.name == selected.name && item.isSelected -> item.copy(
                                isSelected = true
                            )

                            item.name == selected.name && !item.isSelected -> item.copy(
                                isSelected = true
                            )

                            item.name != selected.name && item.isSelected -> item.copy(
                                isSelected = false
                            )

                            else -> item
                        }
                    }

                if (selected.name != "ALL") {
                    filterTaskByCategoryName(selected.name)
                } else {
                    getTaskFromDb()
                }
                selectedCategoryName = selected.name
                categoryAdapter.submitList(categoryTemp)
            }
        }

        rvCategory.adapter = categoryAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            getCategoriesFromDataBase()
        }

        rvTask.adapter = taskAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            getTaskFromDb()
        }
    }

    private fun showCreateUpdateTaskBottomSheetWithCategory(category: String) {
        val createTaskBottomSheet = CreateOrUpdateTaskBottomSheet(
            categoryList = categoriesEntity,
            task = null,
            preSelectedCategory = category,
            onCreateClicked = { taskToBeCreated ->
                val taskEntityToBeInsert = TaskEntity(
                    name = taskToBeCreated.name,
                    category = taskToBeCreated.category
                )
                insertTask(taskEntityToBeInsert)
            },
            onUpdateClicked = {},
            onDeleteClicked = {}
        )
        createTaskBottomSheet.show(
            supportFragmentManager,
            "createTaskBottomSheet"
        )
    }



    private fun updateEmptyViewState() {
        lifecycleScope.launch(Dispatchers.Main) {
            val hasCategories = categoriesEntity.isNotEmpty()
            val hasTasks = taskEntity.isNotEmpty()

            rvCategory.isVisible = hasCategories
            newTask.isVisible = hasCategories

            when {
                !hasCategories -> {
                    ctnEmptyView.isVisible = true
                    ivEmptyCategory.isVisible = true
                    ivEmptyTasks.isVisible = false
                    tvEmptyView.text = getString(R.string.empty_category_title)
                    tvEmptyViewSubtitle.text = getString(R.string.empty_category_description)
                    btnCreateEmpty.text = getString(R.string.create_category)
                    btnCreateEmpty.isVisible = true
                    selectedCategoryName = null
                    newTask.isVisible = false
                }

                hasCategories && !hasTasks -> {
                    ctnEmptyView.isVisible = true
                    ivEmptyCategory.isVisible = false
                    ivEmptyTasks.isVisible = true
                    tvEmptyView.text = getString(R.string.empty_task_title)
                    tvEmptyViewSubtitle.text = getString(R.string.empty_task_description)
                    btnCreateEmpty.text = getString(R.string.add_task_to_category)
                    btnCreateEmpty.isVisible = true
                    newTask.isVisible = false
                }

                else -> {
                    ctnEmptyView.isVisible = false
                    ivEmptyCategory.isVisible = false
                    ivEmptyTasks.isVisible = false
                    btnCreateEmpty.isVisible = false
                    newTask.isVisible = true
                }
            }
        }
    }

    private fun showInfoDialog(
        title: String,
        description: String,
        btnText: String,
        onClick: () -> Unit
    ) {
        val infoBottomSheet = InfoBottomSheet(
            title = title,
            description = description,
            btnText = btnText,
            onClick
        )
        infoBottomSheet.show(
            supportFragmentManager,
            "infoBottomSheet"
        )
    }

    private fun getCategoriesFromDataBase() {
        val categoriesFromDb = categoryDao.getAll()
        categoriesEntity = categoriesFromDb

        val categoriesUiData = categoriesFromDb.map {
            CategoryUiData(
                name = it.name,
                isSelected = it.isSelected
            )
        }.sortedBy { it.name }.toMutableList()
        categoriesUiData.add(
            CategoryUiData(
                name = "+",
                isSelected = false
            )
        )

        val categoryListTemp = mutableListOf(
            CategoryUiData(
                name = "ALL",
                isSelected = true
            )
        )
        categoryListTemp.addAll(categoriesUiData)
        lifecycleScope.launch(Dispatchers.Main) {
            categories = categoryListTemp
            categoryAdapter.submitList(categories)
            selectedCategoryName = "ALL"
            updateEmptyViewState()
        }
    }

    private fun getTaskFromDb() {
        lifecycleScope.launch(Dispatchers.IO) {
            val taskFromDb: List<TaskEntity> = taskDao.getAll()
            taskEntity = taskFromDb
            val taskUiData: List<TaskUiData> = taskFromDb.map {
                TaskUiData(
                    id = it.id,
                    name = it.name,
                    category = it.category
                )
            }.sortedBy { it.name }
            withContext(Dispatchers.Main) {
                selectedCategoryName = "ALL"
                tasks = taskUiData
                taskAdapter.submitList(taskUiData)
                newTask.isVisible = false
                updateEmptyViewState()
            }

        }
    }

    private fun insertCategory(categoryEntity: CategoryEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val existing = categoryDao.getAll().find { it.name.equals(categoryEntity.name, ignoreCase = true) }
            if (existing == null) {
                categoryDao.insert(categoryEntity)
                getCategoriesFromDataBase()
            } else {
                withContext(Dispatchers.Main) {
                    Snackbar.make(newTask, "Category already exists!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteCategory(categoryEntity: CategoryEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val tasksToBeDeleted = taskDao.getAllByCategoryName(categoryEntity.name)
            taskDao.deleteAll(tasksToBeDeleted)
            categoryDao.delete(categoryEntity)
            getCategoriesFromDataBase()
            getTaskFromDb()
        }
    }

    private fun filterTaskByCategoryName(category: String) {
        selectedCategoryName = category
        lifecycleScope.launch(Dispatchers.IO) {
            val tasksFromDb: List<TaskEntity> = taskDao.getAllByCategoryName(category)
            val taskUiData = tasksFromDb.map {
                TaskUiData(
                    id = it.id,
                    name = it.name,
                    category = it.category
                )
            }
            withContext(Dispatchers.Main) {
                tasks = taskUiData
                taskAdapter.submitList(taskUiData)

                if (taskUiData.isEmpty()) {
                    ctnEmptyView.isVisible = true
                    ivEmptyCategory.isVisible = false
                    ivEmptyTasks.isVisible = true
                    tvEmptyView.text = getString(R.string.empty_task_title)
                    tvEmptyViewSubtitle.text = getString(R.string.category_empty_task_description)
                    btnCreateEmpty.text = getString(R.string.add_task_to_category)
                    btnCreateEmpty.isVisible = true
                    newTask.isVisible = false
                } else {
                    ctnEmptyView.isVisible = false
                    ivEmptyCategory.isVisible = false
                    ivEmptyTasks.isVisible = false
                    btnCreateEmpty.isVisible = false
                    newTask.isVisible = true
                }

            }
        }
    }

    private fun insertTask(taskEntity: TaskEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            taskDao.insert(taskEntity)
            if (selectedCategoryName == "ALL") {
                getTaskFromDb()
            } else {
                filterTaskByCategoryName(selectedCategoryName ?: "ALL")
            }
        }
    }

    private fun upDateTask(taskEntity: TaskEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            taskDao.upDate(taskEntity)
            if (selectedCategoryName == "ALL") {
                getTaskFromDb()
            } else {
                filterTaskByCategoryName(selectedCategoryName ?: "ALL")
            }
        }
    }

    private fun deleteTask(taskEntityToBeDelete: TaskEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            taskDao.delete(taskEntityToBeDelete)
            getTaskFromDb()
        }
    }

    private fun showCreateUpdateTaskBottomSheet(taskUiData: TaskUiData? = null) {
        val createTaskBottomSheet = CreateOrUpdateTaskBottomSheet(
            task = taskUiData,
            categoryList = categoriesEntity,
            onCreateClicked = { taskToBeCreated ->
                val taskEntityToBeInsert = TaskEntity(
                    name = taskToBeCreated.name,
                    category = taskToBeCreated.category
                )
                insertTask(taskEntityToBeInsert)
            },
            onUpdateClicked = { taskToBeUpDated ->
                val taskEntityToBeUpdate = TaskEntity(
                    id = taskToBeUpDated.id,
                    name = taskToBeUpDated.name,
                    category = taskToBeUpDated.category
                )
                upDateTask(taskEntityToBeUpdate)
            },
            onDeleteClicked = { taskToBeDeleted ->
                val taskEntityToBeDelete = TaskEntity(
                    id = taskToBeDeleted.id,
                    name = taskToBeDeleted.name,
                    category = taskToBeDeleted.category
                )
                deleteTask(taskEntityToBeDelete)
            }
        )
        createTaskBottomSheet.show(
            supportFragmentManager,
            "createTaskBottomSheet"
        )
    }

    private fun showCreateCategoryBottonSheet() {
        val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->
            val newCategory = CategoryEntity(
                name = categoryName,
                isSelected = false
            )
            insertCategory(newCategory)
        }
        createCategoryBottomSheet.show(
            supportFragmentManager,
            "createCategoryBottomSheet"
        )
    }
}