pipeline {
    agent any

    environment {
        FLASK_IMAGE = "${params.FLASK_IMAGE}"
        NGINX_IMAGE = "${params.NGINX_IMAGE}"
    }

    stages {
        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Containers via Docker Compose') {
            steps {
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }

        stage('Test Request') {
            steps {
                script {
                    def code = sh(script: 'curl -s -o /dev/null -w "%{http_code}" http://localhost:8081', returnStdout: true).trim()
                    if (code != '200') {
                        error("Request failed with status: ${code}")
                    }
                    echo "Request succeeded with 200 OK"
                }
            }
        }

        stage('Tear Down') {
            steps {
                sh 'docker compose down'
            }
        }
    }
}
