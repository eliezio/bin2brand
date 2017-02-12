pipeline {
    agent any

    tools {
        jdk 'OpenJDK 8'
    }

    options {
        timestamps()
    }

    stages {

        stage('build') {
            steps {
                sh './gradlew clean test build'
            }
            post {
                success {
                    junit 'build/test-results/test/*.xml,build/test-results/integrationTest/*.xml'
                }
            }
        }

        stage('sonar') {
            steps {
                withSonarQubeEnv('sq1') {
                    sh "./gradlew --info --stacktrace sonarqube"
                }
            }
        }

        stage('docker image build & publish') {
            steps {
                sh './gradlew --stacktrace -Ddocker.host=$DOCKER_HOST -Ddocker.registry=$DOCKER_REGISTRY dockerPushImage'
            }
        }
    }
}
