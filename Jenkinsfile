// Jenkinsfile - CarRental Project CI/CD Pipeline
// For DevOps course demonstration
// Updated for Java 21

pipeline {
    agent {
        node {
            label 'worker1'
        }
    }

    // Using Worker VM's system JDK 21 (no Jenkins configuration needed)

    environment {
        PROJECT_NAME = 'CarRental'
    }

    stages {
        // ===== Stage 1: Checkout =====
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
                sh 'ls -la'
            }
        }

        // ===== Stage 2: Build =====
        stage('Build') {
            steps {
                echo 'Compiling project...'
                sh 'chmod +x gradlew'
                sh './gradlew clean compileJava -x test'
            }
            post {
                success {
                    echo 'Build successful!'
                }
                failure {
                    echo 'Build failed!'
                }
            }
        }

        // ===== Stage 3: Test =====
        stage('Test') {
            steps {
                echo 'Running unit tests...'
                sh './gradlew test'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit '**/build/test-results/test/*.xml'
                    // Note: HTML Publisher Plugin not installed, removed publishHTML
                }
                success {
                    echo 'All tests passed!'
                }
                failure {
                    echo 'Tests failed! Check the test report for details.'
                }
            }
        }

        // ===== Stage 4: Package =====
        stage('Package') {
            steps {
                echo 'Packaging application...'
                sh './gradlew jar'
                sh 'ls -la build/libs/'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                    echo 'Package created successfully!'
                }
            }
        }

        // ===== Stage 5: Build Info =====
        stage('Build Info') {
            steps {
                echo 'Build Information:'
                echo " - Project: ${PROJECT_NAME}"
                echo " - Build Number: ${BUILD_NUMBER}"
                echo " - Workspace: ${WORKSPACE}"
                sh 'java -version'
            }
        }
    }

    // Pipeline post-processing
    post {
        always {
            echo 'Cleaning workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed! Please check the logs.'
        }
    }
}
