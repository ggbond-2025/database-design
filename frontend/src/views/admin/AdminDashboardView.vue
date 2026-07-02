<template>
  <section class="page-container">
    <header class="page-header">
      <div>
        <h1>管理仪表盘</h1>
        <p>从基础数据、开课安排、选课成绩到统计验收的完整管理入口。</p>
      </div>
    </header>

    <div class="dashboard-grid">
      <RouterLink class="dashboard-card" to="/admin/students">
        <strong>学生与班级</strong>
        <span>维护学生、班级、专业和地区基础数据</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/admin/assignments">
        <strong>开课与选课</strong>
        <span>发布课程安排，处理学生选课记录</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/admin/grades">
        <strong>成绩与学分</strong>
        <span>管理成绩录入，触发学分自动汇总</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/admin/statistics">
        <strong>统计验收</strong>
        <span>查看地区人数、平均分、排名和任课视图</span>
      </RouterLink>
    </div>
  </section>
</template>
