pipeline {
    agent any 
    environment {
        GitURL = 'https://github.com/galfrylich/sigmabit-task'
        GitcredentialsId = 'afa64820-bb7c-4392-adc2-e997ceb75066'
        DockerHubCred = 'docker-hub'
    }
    
    stages {
        stage('Git Checkout') {
            steps {
                echo '# # # # # STAGE 1 - Git checkout # # # # #'
                checkout([$class: 'GitSCM',
                 branches: [[name: '*/main']], 
                 extensions: [], 
                 userRemoteConfigs: [[credentialsId: "$GitcredentialsId",
                 url: "$GitURL"]]])
            }
        }
        stage('Build') {
            steps {
                script {
                    echo '# # # # # STAGE 2 - Build Image # # # # #'
                def imageName = "flask_app:${BUILD_NUMBER}"
                echo "Building Docker image: ${imageName}"
                dockerImage = docker.build(imageName, ".")  
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                echo '# # # # # STAGE 3 - Push Image # # # # #'
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DockerHubCred) {
                        echo "Pushing Docker image with tag: ${dockerImage.tag}"
                        dockerImage.push()           // push build tag, e.g. flask_app:42
                    }
                }
            }
        }



        
}
}