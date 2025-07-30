pipeline {
    agent any

    parameters {
        string(name: 'IMAGE_NAME', defaultValue: 'galfrylich/flask-app', description: 'Docker image name')
        string(name: 'DOCKERHUB_CRED_ID', defaultValue: 'dockerhub-creds-id', description: 'Jenkins credential ID for DockerHub')
    }

    environment {
        IMAGE_NAME = "${params.IMAGE_NAME}"
        DOCKERHUB_CRED_ID = "${params.DOCKERHUB_CRED_ID}"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building image ${IMAGE_NAME}:${IMAGE_TAG}"
                    docker.build("${IMAGE_NAME}:${IMAGE_TAG}")
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CRED_ID) {
                        docker.image("${IMAGE_NAME}:${IMAGE_TAG}").push()
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Successfully built and pushed ${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "❌ Build or push failed"
        }
    }
}