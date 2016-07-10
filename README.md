AWS Stream library
==================
Provides ability to lazily stream data from AWS. Can be used with different AWS Services.



    AmazonECSClient ecsClient = new AmazonECSClient();

    ListTaskDefinitionsRequest req = new ListTaskDefinitionsRequest();

    Stream<String> stream = AwsStreamFactory.createStream(ecsClient::listTaskDefinitions, req,
	resp -> resp.getTaskDefinitionArns().iterator());
    stream.forEach(System.out::println);

