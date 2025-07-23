pipeline {
    agent any 
    environment {
        GitURL = 'https://github.com/galfrylich/sigmabit-task'
        GitcredentialsId = 'afa64820-bb7c-4392-adc2-e997ceb75066'
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
                 sh 'git branch -a'
                 sh 'ls -al'

            }
        }
        
}
}