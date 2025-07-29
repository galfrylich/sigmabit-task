

pipelineJob('build-and-push-flask') {
    definition {
        cps {
            script('''
                pipeline {
                    agent any
                    environment {
                        GIT_CRED_ID = 'afa64820-bb7c-4392-adc2-e997ceb75066'
                        DOCKERHUB_CRED_ID = 'ee194876-8dee-4634-b1cf-535ea8fe0f67'
                        IMAGE_NAME = 'galfrylich/sigmabit-task'
                    }
                    stages {
                        stage('Clone') {
                            steps {
                                checkout([$class: 'GitSCM',
                                    branches: [[name: '*/main']],
                                    userRemoteConfigs: [[
                                        url: 'https://github.com/galfrylich/sigmabit-task',
                                        credentialsId: "${GIT_CRED_ID}"
                                    ]]
                                ])
                            }
                        }
                        stage('Build & Push') {
                            steps {
                                script {
                                    def image = docker.build("${IMAGE_NAME}:${BUILD_NUMBER}")
                                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKERHUB_CRED_ID}") {
                                        image.push()
                                    }
                                }
                            }
                        }
                    }
                }
            ''')
        }
    }
}

pipelineJob('build-and-push-nginx') {
    definition {
        cps {
            script('''
                pipeline {
                    agent any
                    environment {
                        GIT_CRED_ID = 'afa64820-bb7c-4392-adc2-e997ceb75066'
                        DOCKERHUB_CRED_ID = 'ee194876-8dee-4634-b1cf-535ea8fe0f67'
                        IMAGE_NAME = 'galfrylich/nginx-proxy'
                    }
                    stages {
                        stage('Clone') {
                            steps {
                                checkout([$class: 'GitSCM',
                                    branches: [[name: '*/main']],
                                    userRemoteConfigs: [[
                                        url: 'https://github.com/galfrylich/sigmabit-task',
                                        credentialsId: "${GIT_CRED_ID}"
                                    ]]
                                ])
                            }
                        }
                        stage('Build NGINX') {
                            steps {
                                script {
                                    def nginxImage = docker.build("${IMAGE_NAME}:${BUILD_NUMBER}", "-f Dockerfile.nginx .")
                                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKERHUB_CRED_ID}") {
                                        nginxImage.push()
                                    }
                                }
                            }
                        }
                    }
                }
            ''')
        }
    }
}

pipelineJob('run-containers-and-test') {
    definition {
        cps {
            script('''
                pipeline {
                    agent any
                    environment {
                        GIT_CRED_ID = 'afa64820-bb7c-4392-adc2-e997ceb75066'
                        FLASK_IMAGE = 'galfrylich/sigmabit-task'
                        NGINX_IMAGE = 'galfrylich/nginx-proxy'
                    }
                    stage('Clean Workspace') {
                            steps {
                                deleteDir()  // 🔧 This ensures old files are removed
                            }
                        }
                    stages {
                        stage('Checkout') {
                            steps {
                                checkout([$class: 'GitSCM',
                                    branches: [[name: '*/main']],
                                    userRemoteConfigs: [[
                                        url: 'https://github.com/galfrylich/sigmabit-task',
                                        credentialsId: "${GIT_CRED_ID}"
                                    ]]
                                ])
                            }
                        }

                        stage('Run Containers via Docker Compose') {
                            steps {
                                sh 'ls -l'
                                sh 'docker compose down || true'  // Stop any existing containers
                                sh 'docker compose up -d'         // Start fresh
                            }
                        }

                        stage('Test Request') {
                            steps {
                                script {
                                    def output = sh(script: 'curl -s -o -v /dev/null -w "%{http_code}" http://localhost:8081', returnStdout: true).trim()
                                    if (output != '200') {
                                        error("Request failed with status: ${output}")
                                    } else {
                                        echo "Request succeeded with 200 OK"
                                    }
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
            ''')
        }
    }
}

