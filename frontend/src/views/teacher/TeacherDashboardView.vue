<template>
  <section class="page-container">
    <header class="page-header">
      <div>
        <h1>教师工作台</h1>
        <p>从任课安排进入选课名单、成绩录入和课程统计。</p>
      </div>
    </header>
    <div class="dashboard-grid">
      <RouterLink class="dashboard-card" to="/teacher/assignments">
        <strong>我的任课</strong>
        <span>查看课程、班级、学年学期和平均分</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/teacher/enrollments">
        <strong>选课名单</strong>
        <span>按开课ID查看课程下的学生名单</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/teacher/grades">
        <strong>成绩录入</strong>
        <span>为本人任课课程录入或修改学生成绩</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/teacher/statistics">
        <strong>课程统计</strong>
        <span>查看平均分和成绩排名</span>
      </RouterLink>
    </div>
  </section>
</template>
