pipeline {
    agent any
    tools {
        jdk 'jdk17'
    }
    stages {
        stage('1. checkout'){
            steps {
                checkout scmGit(
                        branches: [[name: '*/main']],
                        userRemoteConfigs: [[credentialsId: 'github-token', url:'https://github.com/Minbro-Kim/cicd-prac-repo.git']]
                )
            }
        }
        stage('2. Build & Test'){
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }
        stage('3. Docker Build'){
            steps {
                sh 'docker build -t my-discodeit-app .'
            }
        }
        stage('4. Deploy'){
            steps{
                script {
                    sh 'docker rm -f my-running-app || true'
                    sh 'docker run -d --name my-running-app -p 8081:8080 my-discodeit-app'
                }
            }
        }
    }
    post {
        always {
            jacoco()
        }
    }
}