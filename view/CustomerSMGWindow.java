String filename = reader.getFilenames(customer.getUsername(), store.getStoreName());
        String otherFilename = reader.getFilenames(store.getSeller().getUsername(), customer.getUsername());
        File file = new File(filename);
        File otherFile = new File(otherFilename);
        if (!file.exists() && otherFile.exists()) { // update file from otherFile
            file.createNewFile();
            BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
            String formatHeader = String.format("%s,%s,%s\n", "timestamp",
                    "username", "message");
            List<String> messages = reader.readMessages(otherFilename);
            bwr.write(formatHeader);
            writer.updateConversationFile(filename, messages);
            bwr.close();

        } else if (!otherFile.exists() && file.exists()) { // update otherFile from file
            otherFile.createNewFile();
            BufferedWriter bwr = new BufferedWriter(new FileWriter(otherFile));
            String formatHeader = String.format("%s,%s,%s\n", "timestamp",
                    "username", "message");
            List<String> messages = reader.readMessages(filename);
            bwr.write(formatHeader);
            writer.updateConversationFile(otherFilename, messages);
            bwr.close();

        } else { // both of two file do not exist
            file.createNewFile();
            otherFile.createNewFile();
            BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
            BufferedWriter bwf = new BufferedWriter(new FileWriter(otherFile));
            String formatHeader = String.format("%s,%s,%s\n", "timestamp",
                    "username", "message");
            bwr.write(formatHeader);
            bwf.write(formatHeader);
            bwr.close();
            bwf.close();
        }
