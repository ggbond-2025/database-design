<template>
  <section class="page-container">
    <header class="page-header">
      <div>
        <h1>学生中心</h1>
        <p>完成选课、退课、成绩查询、学分统计、班级名单和排名查询闭环。</p>
      </div>
    </header>
    <div class="dashboard-grid">
      <RouterLink class="dashboard-card" to="/student/available-courses">
        <strong>可选课程</strong>
        <span>查看开放选课课程并提交选课</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/student/enrollments">
        <strong>我的选课</strong>
        <span>查看状态并对未录成绩课程退课</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/student/grades">
        <strong>我的成绩</strong>
        <span>查询课程成绩、教师和学分</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/student/credits">
        <strong>我的学分</strong>
        <span>查看已修学分、毕业所需学分和剩余学分</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/student/classmates">
        <strong>我的班级</strong>
        <span>查看本班学生姓名和学号</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/student/rank">
        <strong>我的排名</strong>
        <span>选择已选课程查看成绩排名</span>
      </RouterLink>
    </div>
  </section>
</template>
