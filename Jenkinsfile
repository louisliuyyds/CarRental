// Jenkinsfile - CarRental项目CI/CD流水线
// 用于DevOps课程演示

pipeline {
    agent any

    // 工具配置（需要在Jenkins全局配置中添加JDK和Gradle）
    tools {
        jdk 'JDK17'
        gradle 'Gradle'
    }

    // 环境变量
    environment {
        PROJECT_NAME = 'CarRental'
    }

    stages {
        // ===== Stage 1: 代码检出 =====
        stage('Checkout') {
            steps {
                echo '📦 检出代码...'
                checkout scm
                sh 'ls -la'
            }
        }

        // ===== Stage 2: 编译构建=====
        stage('Build') {
            steps {
                echo '🔨 编译项目...'
                sh 'chmod +x gradlew'
                sh './gradlew clean compileJava -x test'
            }
            post {
                success {
                    echo '✅ 编译成功！'
                }
                failure {
                    echo '❌ 编译失败！'
                }
            }
        }

        // ===== Stage 3: 单元测试=====
        stage('Test') {
            steps {
                echo '🧪 运行单元测试...'
                sh './gradlew test'
            }
            post {
                always {
                    // 发布JUnit测试报告
                    junit '**/build/test-results/test/*.xml'

                    // 发布HTML测试报告
                    publishHTML target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report'
                    ]
                }
                success {
                    echo '✅ 所有测试通过！'
                }
                failure {
                    echo '❌ 测试失败！查看测试报告了解详情。'
                }
            }
        }

        // ===== Stage 4: 打包 =====
        stage('Package') {
            steps {
                echo '📦 打包应用...'
                sh './gradlew jar'

                // 显示生成的JAR文件
                sh 'ls -la build/libs/'
            }
            post {
                success {
                    // 归档构建产物
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                    echo '✅ 打包完成！'
                }
            }
        }

        // ===== Stage 5: 构建信息 =====
        stage('Build Info') {
            steps {
                echo '📊 构建信息:'
                echo "  - 项目: ${PROJECT_NAME}"
                echo "  - 构建号: ${BUILD_NUMBER}"
                echo "  - 工作空间: ${WORKSPACE}"
                sh 'java -version'
            }
        }
    }

    // 整体流水线后处理
    post {
        always {
            echo '🧹 清理工作空间...'
            cleanWs()
        }
        success {
            echo '🎉 流水线执行成功！'
        }
        failure {
            echo '💥 流水线执行失败！请检查日志。'
        }
    }
}
