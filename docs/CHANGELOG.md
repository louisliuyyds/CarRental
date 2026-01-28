# CarRental - 更新历史

## [1.1.0] - 2026-01-28

### 新增功能

#### 顾客界面
- **车辆过滤功能**
  - 添加Kategorie过滤下拉框
  - 简化为只支持按类别过滤，提升可靠性
  - 自动应用过滤，无需额外点击

- **合同草稿系统（ANGELEGT状态）**
  - 新增"Als Entwurf speichern"按钮
  - 草稿状态下车辆状态保持VERFUEGBAR
  - 支持"Buchung fortsetzen"完成预订

- **密码修改功能**
  - 新密码和确认密码验证
  - 禁止空密码
  - 对话框尺寸优化（500x280）
  - 实时数据库更新

- **Vertragsdetails完整显示**
  - 修复Fahrzeugtyp未正确加载的bug
  - 显示完整车辆信息（制造商、型号、类别、驱动类型、座位数、单日价格）
  - 老年人友好的颜色方案（黑色+深蓝+绿色+红色）
  - 弹窗尺寸优化（800x600）

- **UI优化**
  - "Verfügbare Fahrzeuge" → "Autos suchen"
  - "Buchung fortsetzen"按钮文字优化
  - "Stornieren"按钮防止取消进行中的合同（德语错误提示）
  - "Details"按钮新增（显示Vertragsdetails）
  - 用户名只读显示（灰色背景）
  - Meine Daten选项卡支持滚动浏览

#### 员工界面
- **Nutzerverwaltung选项卡**
  - 新增第4个选项卡
  - 完整显示客户信息（12列）
  - 查看Kundendetails功能
  - 统计卡片点击跳转到Nutzerverwaltung

- **车辆过滤功能**
  - 新增按状态过滤对话框
  - 支持过滤：VERFUEGBAR, VERMIETET, WARTUNG, IN_REPARATUR
  - Fahrzeugtyp下拉框显示Kategorie信息

- **统计功能增强**
  - Gesamte Fahrzeuge → Fahrzeugverwaltung
  - Aktive Verträge → Vertragsverwaltung（自动过滤LAUFEND状态）
  - Verfügbare Fahrzeuge → Fahrzeugverwaltung（自动过滤VERFUEGBAR状态）
  - Registrierte Kunden → Nutzerverwaltung

#### 系统功能
- **ContractStatusUpdater**
  - 自动检查和更新合同状态
  - 定期任务调度
  - 邮件通知支持

- **自定义日历组件**
  - CalendarPanel：月份视图日历
  - CalendarDateChooser：日期选择器
  - 集成到注册表单和个人信息界面

- **数据库改进**
  - 修复MietvertragDao的Fahrzeugtyp映射问题
  - 完整的JOIN查询包含所有关系数据
  - 调试日志输出增强

### Bug修复

1. **严重Bug：Vertragsdetails只显示车牌号**
   - 原因：MietvertragDao未正确加载Fahrzeugtyp
   - 解决：完整实现Fahrzeugtyp映射逻辑（Hersteller, Modell, Kategorie等）
   - 影响：所有Vertragsdetails显示

2. **Bug：密码对话框标签被挤压**
   - 原因：输入框preferredSize过大（350px）在500px对话框中
   - 解决：调整输入框宽度为280px，使用GridBagConstraints.weightx
   - 影响：密码修改对话框

3. **Bug：车辆类型信息显示错误**
   - 原因：FahrzeugPanel下拉框未显示Kategorie
   - 解决：下拉框渲染器添加"（Kategorie）"后缀
   - 影响：车辆创建和选择

4. **Bug：过滤下拉框重复显示选项**
   - 原因：updateFilterValues()方法被频繁调用且缺少防重复检查
   - 解决：简化为只支持Kategorie过滤，移除Hersteller过滤
   - 影响：Auto suchen选项卡

5. **Bug：UI元素被车辆列表遮挡**
   - 原因：filterPanel未设置固定高度，组件换行
   - 解决：设置filterPanel.setPreferredSize(-1, 100)
   - 影响：Auto suchen选项卡

6. **Bug：顾客可以取消进行中的合同**
   - 原因：缺少状态检查
   - 解决：添加LAUFEND和BESTAETIGT状态检查，德语错误提示
   - 影响：Meine Buchungen选项卡

7. **Bug：Meine Daten部分信息不可见**
   - 原因：form面板未放入JScrollPane
   - 解决：将form放入JScrollPane，设置垂直滚动策略
   - 影响：Meine Daten选项卡

8. **Bug：合同完成功能中Fahrzeugtyp未加载**
   - 原因：MietvertragDao不完整
   - 解决：完整加载所有Fahrzeugtyp和Fahrzeug字段
   - 影响：所有合同相关功能

### 改进

#### 用户体验
- 老年人友好设计（颜色、字体、对比度）
- 德语本地化的错误提示
- 更清晰的UI标签和按钮
- 改进的可访问性

#### 代码质量
- 增强的调试输出（控制台日志）
- 详细的错误处理和提示
- 改进的SQL查询性能（JOIN优化）
- 更完善的文档注释

#### 性能优化
- 数据库连接管理优化
- 资源释放改进（try-with-resources）
- 字符串处理优化

### 开发统计
- **新增代码**: 约1,500行
- **修改文件**: 6个文件
- **Bug修复**: 8个
- **新增视图**: 2个（CalendarPanel, CalendarDateChooser）
- **新增控制器**: 1个（ContractStatusUpdater）

### 技术债务
- 待实现：密码加密（当前为明文存储）
- 待实现：邮件通知发送
- 待实现：自动化测试套件

---

## [1.0.0] - 2025-12-15

### 初始版本

- 完整的MVC架构实现
- 基础的Kunde和Mitarbeiter功能
- Fahrzeug- und Fahrzeugtypverwaltung
- Mietvertrag-Management
- Swing GUI实现
- IBM Db2数据库集成
- Maven构建系统
