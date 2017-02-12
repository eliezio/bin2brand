pipeline {
    agent any

    tools {
        jdk 'OpenJDK 8'
    }

    options {
        timestamps()
    }

    stages {

        stage('Build') {
            steps {
                sh './gradlew clean test build'
            }
            post {
                success {
                    junit 'build/test-results/test/*.xml,build/test-results/integrationTest/*.xml'
                }
            }
        }

        stage('Code Coverage') {
            steps {
                step([$class: 'JacocoPublisher', execPattern: 'build/jacoco/*.exec', classPattern: 'build/classes/main', sourcePattern: '**/src/main/java'])
            }
        }

        stage('Static Code Aanlysis') {
            steps {
                withSonarQubeEnv('sq1') {
                    sh "./gradlew --info --stacktrace sonarqube"
                }
            }
        }

        stage('Docker Build & Publish') {
            steps {
                sh './gradlew --stacktrace -Ddocker.host=$DOCKER_HOST -Ddocker.registry=$DOCKER_REGISTRY dockerPushImage'
            }
        }
    }
}
