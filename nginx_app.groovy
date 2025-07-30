pipeline {
    agent any

    environment {
        IMAGE_NAME = "${params.IMAGE_NAME}"
        DOCKERHUB_CRED_ID = "${params.DOCKERHUB_CRED_ID}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Nginx Image') {
            steps {
                script {
                    docker.build("${IMAGE_NAME}:${BUILD_NUMBER}", "-f Dockerfile.nginx .")
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CRED_ID) {
                        docker.image("${IMAGE_NAME}:${BUILD_NUMBER}").push()
                    }
                }
            }
        }
    }
}
