package com.argesurec.shared.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    val appName: String
    val home: String
    val projects: String
    val tasks: String
    val team: String
    val reports: String
    val profile: String
    val settings: String
    val login: String
    val register: String
    val email: String
    val password: String
    val welcomeBack: String
    val createProject: String
    val addMember: String
    val premium: String
    val cancel: String
    val milestones: String
    val files: String
    val expenses: String
    val save: String
    val delete: String
    val error: String
    val success: String
    val loginInstructions: String
    val rememberMe: String
    val noAccount: String
    val signUpNow: String
    val upgradeToPremium: String
    val premiumFeatures: String
    val projectLimit: String
    val teamLimit: String
    val storageLimit: String
    val aiAnalysis: String
    val exportReports: String
    val buyNow: String
    val restorePurchases: String
    val logout: String
    val removeLimits: String
    val dashboard: String
    val premiumTagline: String
    val bestValue: String
    val monthly: String
    val yearly: String
    val free: String
    val user: String
    val executiveDashboard: String
    val ecosystemPerformance: String
    val newInitiative: String
    val activeProjects: String
    val pendingTasks: String
    val completed: String
    val delayed: String
    val totalProjects: String
    val assignedToYou: String
    val actionRequired: String
    val assignedTasks: String
    val projectRoadmap: String
    val projectProgress: String
    val incubation: String
    val development: String
    val commercialization: String
    val unknown: String
    val hello: String
    val viewAll: String
    val highVisibility: String
    val critical: String
    val success_label: String
    val visibility: String
    val all: String
    val newProject: String
    val new_short: String
    val loadingProjects: String
    val noProjectsFound: String
    val progress: String
    val budget: String
    val spent: String
    val noDescription: String
    val startNewProject: String
    val enterProjectDetails: String
    val projectName: String
    val projectDescription: String
    val totalBudget: String
    val currentSpending: String
    val startDate: String
    val endDate: String
    val projectPhase: String
    val createProjectButton: String
    val projectNamePlaceholder: String
    val startArgeProcess: String
    val tasks_count: (Int) -> String
    val teamManagement: String
    val membersIncludedInProject: String
    val allTeamMembers: String
    val searchMember: String
    val addNewMember: String
    val selectProjectToAddMember: String
    val memberPersonnel: String
    val role: String
    val joined: String
    val actions: String
    val noResultsFound: String
    val unnamed: String
    val argeDepartment: String
    val addMemberToTeam: String
    val enterEmailAndAssignRole: String
    val emailAddress: String
    val emailPlaceholder: String
    val projectRole: String
    val add: String
    val removeMember: String
    val removeMemberConfirm: (String) -> String
    val editRole: String
    val selectNewRoleFor: (String) -> String
    val newRole: String
    val updateRole: String
    val loadingTeam: String
    val efficiencyAndReports: String
    val loadingReports: String
    val portfolioEfficiency: String
    val teamScore: String
    val budgetUsage: String
    val riskLevel: String
    val riskTrendStable: String
    val portfolioHealth: String
    val projectProgressInfo: String
    val noProjectDataFound: String
    val portfolioInsightsTitle: String
    val portfolioEfficiencyInsight: String
    val details: String
    val accountSettings: String
    val activeMember: String
    val personalInfo: String
    val profileInfo: String
    val profileInfoSubtitle: String
    val appPreferences: String
    val appPreferencesSubtitle: String
    val about: String
    val aboutSubtitle: String
    val editProfile: String
    val fullNameLabel: String
    val department: String
    val darkMode: String
    val darkModeSubtitle: String
    val notifications: String
    val notificationsSubtitle: String
    val close: String
    val appVersion: String
    val appDescription: String
    val copyright: String
    val understood: String
    val defaultUser: String
    val defaultEmail: String
    val deptSoftware: String
    val deptHardware: String
    val deptMechanical: String
    val deptEmbedded: String
    val deptProject: String
    val deptData: String
    val deptQuality: String
    val premiumMonthlyTitle: String
    val premiumMonthlyDesc: String
    val premiumYearlyTitle: String
    val premiumYearlyDesc: String
    val saveTwoMonths: String
    val managementSystem: String
    val emailExample: String
    val passwordDots: String
    val signUp: String
    val back: String
    val manageOrganizationTeam: String
    
    // UI & Navigation
    val kanbanBoard: String
    val allTasks: String
    val milestoneTasks: String
    val organizationWideTasks: String
    val addTask: String
    val loadingTasks: String
    val noTasksFound: String
    val todo: String
    val inProgress: String
    val done: String
    val unassigned: String
    val export: String
    val atRisk: String
    val recentActivities: String
    val timeline: String
    val projectManagement: String
    
    // Premium Features
    val unlimitedProjects: String
    val unlimitedProjectsDesc: String
    val unlimitedTeam: String
    val unlimitedTeamDesc: String
    val advancedAiAnalysis: String
    val advancedAiAnalysisDesc: String
    val customReporting: String
    val customReportingDesc: String
    
    // Organization
    val organizationSetup: String
    val createBusiness: String
    val businessName: String
    val businessNamePlaceholder: String
    val businessDescription: String
    val joiningOrganization: String
    val invitationCode: String
    val noOrganizationFound: String
    val setupInstructions: String
    val creatingOrganization: String
    val organizationCreated: String
    
    // Project Detail
    val edit: String
    val addMilestone: String
    val targetEndDate: String
    val currentStatus: String
    val roadmap: String
    val noMilestones: String
    val active: String
    val onHold: String
    val low: String
    val medium: String
    val high: String
    val stable: String
    val owner: String
    val manager: String
    val successRedirecting: String
    val registerInstructions: String
    val fullNamePlaceholder: String
    val currencySymbol: String
    
    // Components & Actions
    val uploadFile: String
    val addExpense: String
    val projectDocuments: String
    val uploadNewDocument: String
    val uploadInstructions: String
    val noDocumentsFound: String
    val noProjectDescription: String
    val projectTeam: String
    val noTeamMembers: String
    val member: String
    val manageTeam: String
    val download: String
    val overallProjectProgress: String
    val noDateSpecified: String
    val waiting: String
    val milestoneDetail: String
    val goToKanban: String
    val priority: String
    val responsible: String
    val technicalTeam: String
    val notesAndDetails: String
    val defaultMilestoneDescription: String
    val taskDetail: String
    val taskIdLabel: String
    val taskDetailsLabel: String
    val noTaskDescription: String
    val updateStatus: String
    val removeAssignment: String
    val createNewTask: String
    val taskTitle: String
    val taskTitlePlaceholder: String
    val optionalDescription: String
    val milestoneTitleLabel: String
    val budgetAnalysis: String
    val noExpensesFound: String
    val moreRecordsForChart: String
    val expenseTrend: String
    val software: String
    val hardware: String
    val personnel: String
    val service: String
    val other: String
    val descriptionLabel: String
    val categoryLabel: String
    val update: String
    val editProject: String
    val projectPhaseLabel: String
    val memberPlaceholder: String
    val assignedTo: String
    val createdAt: String
    val softwareLabel: String
    val hardwareLabel: String
    val financialTracking: String
    val recentExpenses: String
    val userGuide: String
    val guideSubtitle: String
    val guideStep1Title: String
    val guideStep1Desc: String
    val guideStep2Title: String
    val guideStep2Desc: String
    val guideStep3Title: String
    val guideStep3Desc: String
    val guideStep4Title: String
    val guideStep4Desc: String
    val guideStep5Title: String
    val guideStep5Desc: String
    val gotIt: String
}

class EnStrings : AppStrings {
    override val appName = "ArgeP"
    override val home = "Home"
    override val projects = "Projects"
    override val tasks = "Tasks"
    override val team = "Team"
    override val reports = "Reports"
    override val profile = "Profile"
    override val settings = "Settings"
    override val login = "Login"
    override val register = "Register"
    override val email = "Email"
    override val password = "Password"
    override val welcomeBack = "Welcome Back"
    override val createProject = "Create Project"
    override val addMember = "Add Member"
    override val premium = "Premium"
    override val cancel = "Cancel"
    override val milestones = "Milestones"
    override val files = "Files"
    override val expenses = "Expenses"
    override val save = "Save"
    override val delete = "Delete"
    override val error = "Error"
    override val success = "Success"
    override val loginInstructions = "Please login with your account details."
    override val rememberMe = "Remember me"
    override val noAccount = "Don't have an account?"
    override val signUpNow = "Sign up now"
    override val upgradeToPremium = "Upgrade to Premium"
    override val premiumFeatures = "Premium Features"
    override val projectLimit = "Unlimited Projects (Free: 1)"
    override val teamLimit = "Unlimited Team Members (Free: 3)"
    override val storageLimit = "Unlimited Storage (Free: 10MB/Project)"
    override val aiAnalysis = "AI-Powered R&D Analysis"
    override val exportReports = "Export PDF/Excel Reports"
    override val buyNow = "Buy Now"
    override val restorePurchases = "Restore Purchases"
    override val logout = "Logout"
    override val removeLimits = "Remove Limits"
    override val dashboard = "Dashboard"
    override val premiumTagline = "Manage your R&D projects with unlimited power."
    override val bestValue = "BEST VALUE"
    override val monthly = "month"
    override val yearly = "year"
    override val free = "Free"
    override val user = "User"
    override val executiveDashboard = "Executive Dashboard"
    override val ecosystemPerformance = "Real-time ecosystem performance."
    override val newInitiative = "New Initiative"
    override val activeProjects = "Active Projects"
    override val pendingTasks = "Pending Tasks"
    override val completed = "Completed"
    override val delayed = "Delayed"
    override val totalProjects = "Total projects"
    override val assignedToYou = "Assigned to you"
    override val actionRequired = "Action required"
    override val assignedTasks = "Tasks Assigned to Me"
    override val projectRoadmap = "Project Roadmap"
    override val projectProgress = "Project Progress"
    override val incubation = "Incubation"
    override val development = "Development"
    override val commercialization = "Commercialization"
    override val unknown = "Unknown"
    override val hello = "Hello"
    override val viewAll = "View All"
    override val highVisibility = "High Visibility"
    override val critical = "Critical"
    override val success_label = "Success"
    override val visibility = "Visibility"
    override val all = "All"
    override val newProject = "New Project"
    override val new_short = "New"
    override val loadingProjects = "Loading projects..."
    override val noProjectsFound = "No projects found matching criteria."
    override val progress = "Progress"
    override val budget = "BUDGET"
    override val spent = "SPENT"
    override val noDescription = "No description"
    override val startNewProject = "Start New Project"
    override val enterProjectDetails = "Enter basic details about the project"
    override val projectName = "Project Name"
    override val projectDescription = "Project Goal and Description"
    override val totalBudget = "Total Budget ($)"
    override val currentSpending = "Current Spending ($)"
    override val startDate = "Start Date"
    override val endDate = "Target End Date"
    override val projectPhase = "PROJECT PHASE"
    override val createProjectButton = "Create Project"
    override val projectNamePlaceholder = "e.g., Smart Agriculture Kit"
    override val startArgeProcess = "Start a new R&D process"
    override val tasks_count = { count: Int -> "$count Tasks" }
    override val teamManagement = "Team Management"
    override val membersIncludedInProject = "Members Included in Project"
    override val allTeamMembers = "All Team Members"
    override val searchMember = "Search member..."
    override val addNewMember = "Add New Member"
    override val selectProjectToAddMember = "Select project to add member"
    override val memberPersonnel = "MEMBER / PERSONNEL"
    override val role = "ROLE"
    override val joined = "JOINED"
    override val actions = "ACTIONS"
    override val noResultsFound = "No results found"
    override val unnamed = "Unnamed"
    override val argeDepartment = "R&D Department"
    override val addMemberToTeam = "Add New Member to Team"
    override val enterEmailAndAssignRole = "Enter member's email address and assign a project role."
    override val emailAddress = "Email Address"
    override val emailPlaceholder = "example@company.com"
    override val projectRole = "Project Role"
    override val add = "Add"
    override val removeMember = "Remove Member"
    override val removeMemberConfirm = { name: String -> "Are you sure you want to remove $name from the project?" }
    override val editRole = "Edit Role"
    override val selectNewRoleFor = { name: String -> "Select a new project role for $name." }
    override val newRole = "New Role"
    override val updateRole = "Update Role"
    override val loadingTeam = "Listing team..."
    override val efficiencyAndReports = "Efficiency & Reports"
    override val loadingReports = "Preparing reports..."
    override val portfolioEfficiency = "Portfolio Efficiency"
    override val teamScore = "Team Score"
    override val budgetUsage = "Budget Usage"
    override val riskLevel = "Risk Level"
    override val riskTrendStable = "Stable"
    override val portfolioHealth = "Portfolio Health Status"
    override val projectProgressInfo = "General progress and phase info of projects"
    override val noProjectDataFound = "No project data found yet."
    override val portfolioInsightsTitle = "AI Analysis"
    override val portfolioEfficiencyInsight = "Your portfolio could be managed 12% more efficiently."
    override val details = "Details"
    override val accountSettings = "Account Settings"
    override val activeMember = "ACTIVE MEMBER"
    override val personalInfo = "Personal Information"
    override val profileInfo = "Profile Information"
    override val profileInfoSubtitle = "Update your name, surname and department."
    override val appPreferences = "App Preferences"
    override val appPreferencesSubtitle = "Manage theme and notification settings."
    override val about = "About"
    override val aboutSubtitle = "Version v1.0.4 - Argep Dashboard"
    override val editProfile = "Edit Profile"
    override val fullNameLabel = "Full Name"
    override val department = "Department"
    override val darkMode = "Dark Mode"
    override val darkModeSubtitle = "Reduce eye strain"
    override val notifications = "Notifications"
    override val notificationsSubtitle = "Stay informed about new tasks"
    override val close = "Close"
    override val appVersion = "Version 1.0.4 (Beta)"
    override val appDescription = "Argep is a platform designed to simplify R&D processes and project management."
    override val copyright = "© 2024 Argesurec Technology"
    override val understood = "Understood"
    override val defaultUser = "User"
    override val defaultEmail = "email@app.com"
    override val deptSoftware = "Software Development"
    override val deptHardware = "Hardware & PCB"
    override val deptMechanical = "Mechanical Design"
    override val deptEmbedded = "Embedded Systems"
    override val deptProject = "Project Management"
    override val deptData = "Data Analytics"
    override val deptQuality = "Quality Control"
    override val premiumMonthlyTitle = "Premium Monthly"
    override val premiumMonthlyDesc = "Full access to all premium features."
    override val premiumYearlyTitle = "Premium Yearly"
    override val premiumYearlyDesc = "Get 2 months for free!"
    override val saveTwoMonths = "Get 2 months for free!"
    override val managementSystem = "Management System"
    override val emailExample = "email@example.com"
    override val passwordDots = "••••••••"
    override val signUp = "Sign Up"
    override val back = "Back"
    
    // UI & Navigation
    override val kanbanBoard = "Kanban Board"
    override val allTasks = "All Tasks"
    override val milestoneTasks = "Milestone Tasks"
    override val organizationWideTasks = "Organization-wide tasks"
    override val addTask = "Add Task"
    override val loadingTasks = "Loading tasks..."
    override val noTasksFound = "No tasks found."
    override val todo = "To Do"
    override val inProgress = "In Progress"
    override val done = "Done"
    override val unassigned = "Unassigned"
    override val export = "Export"
    override val atRisk = "At Risk"
    override val recentActivities = "Recent Activities"
    override val timeline = "Timeline"
    override val projectManagement = "Project Management"
    
    // Premium Features
    override val unlimitedProjects = "Unlimited Projects"
    override val unlimitedProjectsDesc = "Manage as many projects as you need without any restrictions."
    override val unlimitedTeam = "Unlimited Team Members"
    override val unlimitedTeamDesc = "Invite your entire organization to collaborate."
    override val advancedAiAnalysis = "Advanced AI Analysis"
    override val advancedAiAnalysisDesc = "Get deep insights and predictions for your R&D projects."
    override val customReporting = "Custom Reporting"
    override val customReportingDesc = "Generate professional PDF/Excel reports in seconds."
    
    // Organization
    override val organizationSetup = "Organization Setup"
    override val createBusiness = "Create Business"
    override val businessName = "Business Name"
    override val businessNamePlaceholder = "e.g., Arge Tech Inc."
    override val businessDescription = "Business Description"
    override val joiningOrganization = "Join an Organization"
    override val invitationCode = "Invitation Code"
    override val noOrganizationFound = "No organization found."
    override val setupInstructions = "To manage your projects, you must first create a business or join an existing one."
    override val creatingOrganization = "Creating organization..."
    override val organizationCreated = "Organization created successfully."
    
    // Project Detail
    override val edit = "Edit"
    override val addMilestone = "Add Milestone"
    override val targetEndDate = "Target End Date"
    override val currentStatus = "Current Status"
    override val roadmap = "Roadmap"
    override val noMilestones = "No roadmap added yet."
    override val active = "ACTIVE"
    override val onHold = "ON HOLD"
    override val low = "Low"
    override val medium = "Medium"
    override val high = "High"
    override val stable = "Stable"
    override val owner = "Owner"
    override val manager = "Manager"
    override val successRedirecting = "Success! Redirecting..."
    override val registerInstructions = "Create a new account"
    override val fullNamePlaceholder = "Your Full Name"
    override val currencySymbol = "$"
    
    // Components & Actions
    override val uploadFile = "Upload File"
    override val addExpense = "Add Expense"
    override val projectDocuments = "Project Documents"
    override val uploadNewDocument = "Upload New Document"
    override val uploadInstructions = "Click to select PDF, DOCX or Image"
    override val noDocumentsFound = "No documents uploaded yet."
    override val noProjectDescription = "No description entered for this project yet."
    override val projectTeam = "Project Team"
    override val noTeamMembers = "No team members added yet."
    override val member = "Member"
    override val manageTeam = "Manage Team"
    override val download = "Download"
    override val overallProjectProgress = "Overall Project Progress"
    override val noDateSpecified = "Date Not Specified"
    override val waiting = "Waiting"
    override val milestoneDetail = "Milestone Detail"
    override val goToKanban = "Go to Kanban"
    override val priority = "Priority"
    override val responsible = "Responsible"
    override val technicalTeam = "Technical Team"
    override val notesAndDetails = "NOTES & DETAILS"
    override val defaultMilestoneDescription = "This milestone aims to complete the basic infrastructure of the project. Database schema, auth module and basic UI navigation should be finished in this phase."
    override val taskDetail = "Task Detail"
    override val taskIdLabel = "Task ID"
    override val taskDetailsLabel = "Task Details"
    override val noTaskDescription = "No description specified."
    override val updateStatus = "UPDATE STATUS"
    override val removeAssignment = "Remove Assignment"
    override val createNewTask = "Create New Task"
    override val taskTitle = "Task Title"
    override val taskTitlePlaceholder = "e.g., Database optimization"
    override val optionalDescription = "Description (Optional)"
    override val milestoneTitleLabel = "Milestone Title"
    override val budgetAnalysis = "Project budget and expense analysis"
    override val noExpensesFound = "No expense records found yet."
    override val moreRecordsForChart = "More records required for chart"
    override val expenseTrend = "EXPENSE TREND"
    override val software = "Software"
    override val hardware = "Hardware"
    override val personnel = "Personnel"
    override val service = "Service"
    override val other = "Other"
    override val descriptionLabel = "Description"
    override val categoryLabel = "CATEGORY"
    override val update = "Update"
    override val editProject = "Edit Project"
    override val projectPhaseLabel = "PROJECT PHASE"
    override val memberPlaceholder = "Member"
    override val assignedTo = "Assigned To"
    override val createdAt = "Created At"
    override val softwareLabel = "SOFTWARE"
    override val hardwareLabel = "HARDWARE"
    override val financialTracking = "Financial Tracking"
    override val recentExpenses = "RECENT EXPENSES"
    override val userGuide = "User Guide"
    override val guideSubtitle = "🚀 ArgeP Adventure Guide"
    override val guideStep1Title = "1. Set Up Your Base"
    override val guideStep1Desc = "First, create an organization to start managing projects."
    override val guideStep2Title = "2. Start a Project"
    override val guideStep2Desc = "Define what you are building, like a rocket or a robot!"
    override val guideStep3Title = "3. Plan Your Steps"
    override val guideStep3Desc = "Break your project into small, reachable milestones."
    override val guideStep4Title = "4. Assign Tasks"
    override val guideStep4Desc = "Create tasks and move them to 'Done' as you finish."
    override val guideStep5Title = "5. Track Success"
    override val guideStep5Desc = "Monitor your budget and team performance with AI."
    override val gotIt = "Got it!"
    override val manageOrganizationTeam = "Add or remove members from your entire organization."
}

class TrStrings : AppStrings {
    override val appName = "ArgeP"
    override val home = "Ana Sayfa"
    override val projects = "Projeler"
    override val tasks = "Görevler"
    override val team = "Ekip"
    override val reports = "Raporlar"
    override val profile = "Profil"
    override val settings = "Ayarlar"
    override val login = "Giriş Yap"
    override val register = "Kayıt Ol"
    override val email = "E-posta"
    override val password = "Şifre"
    override val welcomeBack = "Tekrar Hoş Geldiniz"
    override val createProject = "Proje Oluştur"
    override val addMember = "Üye Ekle"
    override val premium = "Premium"
    override val cancel = "İptal"
    override val milestones = "Milestones"
    override val files = "Dosyalar"
    override val expenses = "Giderler"
    override val save = "Kaydet"
    override val delete = "Sil"
    override val error = "Hata"
    override val success = "Başarılı"
    override val loginInstructions = "Lütfen hesap bilgilerinizle giriş yapın."
    override val rememberMe = "Beni hatırla"
    override val noAccount = "Hesabınız yok mu?"
    override val signUpNow = "Kaydolun"
    override val upgradeToPremium = "Premium'a Yükselt"
    override val premiumFeatures = "Premium Özellikler"
    override val projectLimit = "Sınırsız Proje (Ücretsiz: 1)"
    override val teamLimit = "Sınırsız Ekip Üyesi (Ücretsiz: 3)"
    override val storageLimit = "Sınırsız Depolama (Ücretsiz: 10MB/Proje)"
    override val aiAnalysis = "Yapay Zeka Destekli Ar-Ge Analizi"
    override val exportReports = "PDF/Excel Rapor Çıktısı"
    override val buyNow = "Hemen Satın Al"
    override val restorePurchases = "Satın Almaları Geri Yükle"
    override val logout = "Çıkış Yap"
    override val removeLimits = "Sınırları Kaldırın"
    override val dashboard = "Dashboard"
    override val premiumTagline = "Ar-Ge projelerinizi sınırsız güçle yönetin."
    override val bestValue = "EN İYİ FİYAT"
    override val monthly = "ay"
    override val yearly = "yıl"
    override val free = "Ücretsiz"
    override val user = "Kullanıcı"
    override val executiveDashboard = "Yönetici Paneli"
    override val ecosystemPerformance = "Gerçek zamanlı ekosistem performansı."
    override val newInitiative = "Yeni Girişim"
    override val activeProjects = "Aktif Projeler"
    override val pendingTasks = "Bekleyen Görevler"
    override val completed = "Tamamlanan"
    override val delayed = "Geciken"
    override val totalProjects = "Toplam proje"
    override val assignedToYou = "Size atanan"
    override val actionRequired = "Aksiyon gerekli"
    override val assignedTasks = "Bana Atanan Görevler"
    override val projectRoadmap = "Proje Yol Haritası"
    override val projectProgress = "Proje İlerlemeleri"
    override val incubation = "Kuluçka"
    override val development = "Geliştirme"
    override val commercialization = "Ticarileşme"
    override val unknown = "Bilinmiyor"
    override val hello = "Merhaba"
    override val viewAll = "Tümünü Gör"
    override val highVisibility = "Görünürlük Yüksek"
    override val critical = "Kritik"
    override val success_label = "Başarı"
    override val visibility = "Görünürlük"
    override val all = "Tümü"
    override val newProject = "Yeni Proje"
    override val new_short = "Yeni"
    override val loadingProjects = "Projeler yükleniyor..."
    override val noProjectsFound = "Aranan kriterlerde proje bulunamadı."
    override val progress = "İlerleme"
    override val budget = "BÜTÇE"
    override val spent = "HARCANAN"
    override val noDescription = "Açıklama yok"
    override val startNewProject = "Yeni Proje Başlat"
    override val enterProjectDetails = "Projeye dair temel bilgileri girin"
    override val projectName = "Proje Adı"
    override val projectDescription = "Proje Amacı ve Açıklama"
    override val totalBudget = "Toplam Bütçe (₺)"
    override val currentSpending = "Mevcut Harcama (₺)"
    override val startDate = "Başlangıç Tarihi"
    override val endDate = "Hedef Bitiş Tarihi"
    override val projectPhase = "PROJE FAZI"
    override val createProjectButton = "Projeyi Oluştur"
    override val projectNamePlaceholder = "Örn: Akıllı Tarım Kiti"
    override val startArgeProcess = "Yeni bir Ar-Ge süreci başlatın"
    override val tasks_count = { count: Int -> "$count Görev" }
    override val teamManagement = "Ekip Yönetimi"
    override val membersIncludedInProject = "Projeye Dahil Üyeler"
    override val allTeamMembers = "Tüm Ekip Üyeleri"
    override val searchMember = "Üye ara..."
    override val addNewMember = "Yeni Üye Ekle"
    override val selectProjectToAddMember = "Üye eklemek için proje seçin"
    override val memberPersonnel = "ÜYE / PERSONEL"
    override val role = "ROL"
    override val joined = "KATILMA"
    override val actions = "İŞLEMLER"
    override val noResultsFound = "Sonuç bulunamadı"
    override val unnamed = "İsimsiz"
    override val argeDepartment = "Ar-Ge Departmanı"
    override val addMemberToTeam = "Ekibe Yeni Üye Ekle"
    override val enterEmailAndAssignRole = "Üyenin e-posta adresini girin ve bir proje rolü atayın."
    override val emailAddress = "E-posta Adresi"
    override val emailPlaceholder = "ornek@sirket.com"
    override val projectRole = "Proje Rolü"
    override val add = "Ekle"
    override val removeMember = "Üyeyi Çıkar"
    override val removeMemberConfirm = { name: String -> "$name isimli üyeyi projeden çıkarmak istediğinize emin misiniz?" }
    override val editRole = "Rolü Düzenle"
    override val selectNewRoleFor = { name: String -> "$name için yeni bir proje rolü seçin." }
    override val newRole = "Yeni Rol"
    override val updateRole = "Rolü Güncelle"
    override val loadingTeam = "Ekip listeleniyor..."
    override val efficiencyAndReports = "Verim & Raporlar"
    override val loadingReports = "Raporlar hazırlanıyor..."
    override val portfolioEfficiency = "Portföy Verimi"
    override val teamScore = "Ekip Skoru"
    override val budgetUsage = "Bütçe Kullanımı"
    override val riskLevel = "Risk Seviyesi"
    override val riskTrendStable = "Stabil"
    override val portfolioHealth = "Portföy Sağlık Durumu"
    override val projectProgressInfo = "Projelerin genel ilerleme ve faz bilgileri"
    override val noProjectDataFound = "Henüz proje verisi bulunamadı."
    override val portfolioInsightsTitle = "Yapay Zeka Analizi"
    override val portfolioEfficiencyInsight = "Portföyünüz %12 daha verimli yönetilebilir."
    override val details = "Detay"
    override val accountSettings = "Hesap Ayarları"
    override val activeMember = "AKTİF ÜYE"
    override val personalInfo = "Kişisel Bilgiler"
    override val profileInfo = "Profil Bilgileri"
    override val profileInfoSubtitle = "İsim, soyisim ve departman güncelleyin."
    override val appPreferences = "Uygulama Tercihleri"
    override val appPreferencesSubtitle = "Tema ve bildirim ayarlarını yönetin."
    override val about = "Hakkında"
    override val aboutSubtitle = "Versiyon v1.0.4 - Argep Dashboard"
    override val editProfile = "Profili Düzenle"
    override val fullNameLabel = "Tam İsim"
    override val department = "Departman"
    override val darkMode = "Karanlık Mod"
    override val darkModeSubtitle = "Göz yorgunluğunu azaltın"
    override val notifications = "Bildirimler"
    override val notificationsSubtitle = "Yeni görevlerden haberdar olun"
    override val close = "Kapat"
    override val appVersion = "Sürüm 1.0.4 (Beta)"
    override val appDescription = "Argep, Ar-Ge süreçlerini ve proje yönetimini kolaylaştırmak için tasarlanmış bir platformdur."
    override val copyright = "© 2024 Argesurec Teknoloji"
    override val understood = "Anladım"
    override val defaultUser = "Kullanıcı"
    override val defaultEmail = "email@uygulama.com"
    override val deptSoftware = "Yazılım Geliştirme"
    override val deptHardware = "Hardware & PCB"
    override val deptMechanical = "Mekanik Tasarım"
    override val deptEmbedded = "Gömülü Sistemler"
    override val deptProject = "Proje Yönetimi"
    override val deptData = "Veri Analitiği"
    override val deptQuality = "Kalite Kontrol"
    override val premiumMonthlyTitle = "Aylık Premium"
    override val premiumMonthlyDesc = "Tüm premium özelliklere tam erişim."
    override val premiumYearlyTitle = "Yıllık Premium"
    override val premiumYearlyDesc = "2 ay ücretsiz kazanın!"
    override val saveTwoMonths = "2 ay ücretsiz kazanın!"
    override val managementSystem = "Yönetim Sistemi"
    override val emailExample = "eposta@ornek.com"
    override val passwordDots = "••••••••"
    override val signUp = "Kayıt Ol"
    override val back = "Geri"
    
    // UI & Navigation
    override val kanbanBoard = "Kanban Tahtası"
    override val allTasks = "Tüm Görevler"
    override val milestoneTasks = "Aşama Görevleri"
    override val organizationWideTasks = "Kurum genelindeki görevler"
    override val addTask = "Görev Ekle"
    override val loadingTasks = "Görevler yükleniyor..."
    override val noTasksFound = "Görev bulunamadı."
    override val todo = "Bekliyor"
    override val inProgress = "Devam Ediyor"
    override val done = "Tamamlandı"
    override val unassigned = "Atanmamış"
    override val export = "Dışa Aktar"
    override val atRisk = "Riskli"
    override val recentActivities = "Son Aktiviteler"
    override val manageOrganizationTeam = "Tüm organizasyonunuza üye ekleyin veya çıkarın."
    override val timeline = "Zaman Çizelgesi"
    override val projectManagement = "Proje Yönetimi"
    
    // Premium Features
    override val unlimitedProjects = "Sınırsız Proje"
    override val unlimitedProjectsDesc = "Herhangi bir kısıtlama olmadan dilediğiniz kadar proje yönetin."
    override val unlimitedTeam = "Sınırsız Ekip Üyesi"
    override val unlimitedTeamDesc = "Tüm kurumunuzu iş birliğine davet edin."
    override val advancedAiAnalysis = "Gelişmiş YZ Analizi"
    override val advancedAiAnalysisDesc = "Ar-Ge projeleriniz için derin içgörüler ve tahminler alın."
    override val customReporting = "Özel Raporlama"
    override val customReportingDesc = "Saniyeler içinde profesyonel PDF/Excel raporları oluşturun."
    
    // Organization
    override val organizationSetup = "İşletme Kurulumu"
    override val createBusiness = "İşletme Oluştur"
    override val businessName = "İşletme Adı"
    override val businessNamePlaceholder = "Örn: Arge Teknoloji A.Ş."
    override val businessDescription = "İşletme Açıklaması"
    override val joiningOrganization = "Bir İşletmeye Katıl"
    override val invitationCode = "Davet Kodu"
    override val noOrganizationFound = "Bağlı bir işletme bulunamadı."
    override val setupInstructions = "Projelerinizi yönetmek için önce bir işletme oluşturmalı veya mevcut bir işletmeye katılmalısınız."
    override val creatingOrganization = "İşletme oluşturuluyor..."
    override val organizationCreated = "İşletme başarıyla oluşturuldu."
    
    // Project Detail
    override val edit = "Düzenle"
    override val addMilestone = "Aşama Ekle"
    override val targetEndDate = "Hedef Bitiş"
    override val currentStatus = "Güncel Durum"
    override val roadmap = "Yol Haritası"
    override val noMilestones = "Henüz yol haritası eklenmemiş."
    override val active = "AKTİF"
    override val onHold = "BEKLEMEDE"
    override val low = "Düşük"
    override val medium = "Orta"
    override val high = "Yüksek"
    override val stable = "Stabil"
    override val owner = "İşletme Sahibi"
    override val manager = "Yönetici"
    override val successRedirecting = "Başarılı! Yönlendiriliyor..."
    override val registerInstructions = "Yeni bir hesap oluşturun"
    override val fullNamePlaceholder = "Adınız Soyadınız"
    override val currencySymbol = "₺"
    
    // Components & Actions
    override val uploadFile = "Dosya Yükle"
    override val addExpense = "Harcama Ekle"
    override val projectDocuments = "Proje Belgeleri"
    override val uploadNewDocument = "Yeni Belge Yükle"
    override val uploadInstructions = "PDF, DOCX veya Görsel seçmek için tıklayın"
    override val noDocumentsFound = "Henüz belge yüklenmemiş."
    override val noProjectDescription = "Bu proje için henüz bir açıklama girilmemiş."
    override val projectTeam = "Proje Ekibi"
    override val noTeamMembers = "Henüz ekip üyesi eklenmemiş."
    override val member = "Üye"
    override val manageTeam = "Ekibi Yönet"
    override val download = "İndir"
    override val overallProjectProgress = "Genel Proje İlerlemesi"
    override val noDateSpecified = "Tarih Belirtilmedi"
    override val waiting = "Bekliyor"
    override val milestoneDetail = "Milestone Detayı"
    override val goToKanban = "Kanban'a Git"
    override val priority = "Öncelik"
    override val responsible = "Sorumlu"
    override val technicalTeam = "Teknik Ekip"
    override val notesAndDetails = "NOTLAR & DETAYLAR"
    override val defaultMilestoneDescription = "Bu milestone projenin temel altyapısının tamamlanmasını hedefler. Veritabanı şeması, auth modülü ve temel UI navigasyon bu fazda bitirilmelidir."
    override val taskDetail = "Görev Detayı"
    override val taskIdLabel = "Görev ID"
    override val taskDetailsLabel = "Görev Detayları"
    override val noTaskDescription = "Açıklama belirtilmemiş."
    override val updateStatus = "DURUM GÜNCELLE"
    override val removeAssignment = "Atamayı Kaldır"
    override val createNewTask = "Yeni Görev Oluştur"
    override val taskTitle = "Görev Başlığı"
    override val taskTitlePlaceholder = "Örn: Veritabanı optimizasyonu"
    override val optionalDescription = "Açıklama (Opsiyonel)"
    override val milestoneTitleLabel = "Milestone Başlığı"
    override val budgetAnalysis = "Proje bütçe ve harcama analizi"
    override val noExpensesFound = "Henüz harcama kaydı bulunmuyor."
    override val moreRecordsForChart = "Grafik için daha fazla kayıt gerekli"
    override val expenseTrend = "HARCAMA TRENDİ"
    override val software = "Yazılım"
    override val hardware = "Donanım"
    override val personnel = "Personel"
    override val service = "Hizmet"
    override val other = "Diğer"
    override val descriptionLabel = "Açıklama"
    override val categoryLabel = "KATEGORİ"
    override val update = "Güncelle"
    override val editProject = "Projeyi Düzenle"
    override val projectPhaseLabel = "PROJE AŞAMASI"
    override val memberPlaceholder = "Üye"
    override val assignedTo = "Atanan"
    override val createdAt = "Oluşturulma"
    override val softwareLabel = "YAZILIM"
    override val hardwareLabel = "DONANIM"
    override val financialTracking = "Finansal Takip"
    override val recentExpenses = "SON HARCAMALAR"
    override val userGuide = "Kullanım Kılavuzu"
    override val guideSubtitle = "🚀 ArgeP Macera Rehberi"
    override val guideStep1Title = "1. Karargahı Kur"
    override val guideStep1Desc = "İlk önce projeleri yönetmek için bir işletme oluşturmalısın."
    override val guideStep2Title = "2. Proje Başlat"
    override val guideStep2Desc = "Ne inşa ettiğini tanımla, bir roket mi yoksa bir robot mu?"
    override val guideStep3Title = "3. Adımlarını Planla"
    override val guideStep3Desc = "Projeyi küçük ve ulaşılabilir aşamalara (milestones) böl."
    override val guideStep4Title = "4. İşleri Takip Et"
    override val guideStep4Desc = "Görevler oluştur ve bitirdikçe 'Tamamlandı' kısmına taşı."
    override val guideStep5Title = "5. Başarıyı İzle"
    override val guideStep5Desc = "YZ ile bütçeni ve ekip performansını takip et."
    override val gotIt = "Anladım!"
}

val LocalStrings = staticCompositionLocalOf<AppStrings> { EnStrings() }

@Composable
fun ProvideStrings(content: @Composable () -> Unit) {
    val strings = when (getPlatformLanguage()) {
        "tr" -> TrStrings()
        else -> EnStrings()
    }
    CompositionLocalProvider(LocalStrings provides strings) {
        content()
    }
}

val strings: AppStrings
    @Composable
    @ReadOnlyComposable
    get() = LocalStrings.current
