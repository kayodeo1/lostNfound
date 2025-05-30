# campus-lost-found
A web-based lost and found management system for educational institutions built with JSF, PrimeFaces, and EJB. Students can report lost items, upload found items with photos, and browse through available items with search functionality. Includes an admin panel for item approval and management.
📄 Project Description:
A simple Lost and Found web application built with Java EE (Jakarta EE) technologies including JSF, CDI, EJB, and PrimeFaces. Users can report lost or found items, while an admin interface allows item verification. This demo focuses on clean UI and a functional backend without authentication.

🧱 Features:
📦 View reported lost or found items in a grid

🔍 Search items by keyword

➕ Report a found or lost item

🛂 Admin view for approving or rejecting items

🖼️ Image upload and preview

✅ CDI-managed beans with EJB integration

🎨 PrimeFaces UI with JSF Facelets

🛠️ Technology Stack

Frontend: JSF 2.2, PrimeFaces 8.0
Backend: Java EE 7 (javax), EJB 3.2
Dependency Injection: CDI 1.2
Server: Apache TomEE Plume 7.1.2
Database: JPA/OpenJPA (H2 for development)

Prerequisites

Java 8 or higher
Apache TomEE Plume 7.1.2+
IDE (Eclipse, IntelliJ, NetBeans)

Installation

Clone the repository

bashgit clone https://github.com/yourusername/campus-lost-found.git
cd campus-lost-found

Import project into your IDE as Dynamic Web Project
Deploy to TomEE

bash# Copy WAR to TomEE webapps directory
cp campus-lost-found.war $TOMEE_HOME/webapps/

# Or deploy via TomEE Manager (if enabled)
# http://localhost:8080/manager

Access the application

http://localhost:8080/campus-lost-found
WebContent/
├── WEB-INF/
│   ├── web.xml
│   ├── beans.xml
│   └── faces-config.xml
├── resources/             # CSS, JS, uploads
├── META-INF/
└── *.xhtml               # JSF Views
src/
└── com/kayode/lostNfound/
    ├── bean/             # CDI Managed Beans
    ├── model/            # JPA Entities  
    ├── service/          # EJB Services
    └── util/             # Utility classes
Main Dashboard
Browse and search through reported lost items with an intuitive grid layout.
Report Item Form
Simple form for reporting both lost and found items with image upload capability.
Admin Panel
Administrative interface for managing item submissions and approvals.
🔧 Configuration
Database Setup
The application uses H2 in-memory database by default. For production, configure your persistence.xml:
xml<persistence-unit name="lostNfoundPU">
    <provider>org.apache.openjpa.persistence.PersistenceProviderImpl</provider>
    <jta-data-source>jdbc/LostNFoundDS</jta-data-source>
    <!-- Your entity classes -->
    <properties>
        <property name="openjpa.jdbc.SynchronizeMappings" value="buildSchema(ForeignKeys=true)"/>
    </properties>
</persistence-unit>
File Upload Configuration
Configure file upload limits in web.xml:
xml<context-param>
    <param-name>primefaces.UPLOADER</param-name>
    <param-value>auto</param-value>
</context-param>
🤝 Contributing

Fork the repository
Create a feature branch (git checkout -b feature/amazing-feature)
Commit your changes (git commit -m 'Add some amazing feature')
Push to the branch (git push origin feature/amazing-feature)
Open a Pull Request

📋 TODO

 Email notifications for item matches
 User authentication and profiles
 Mobile-responsive design improvements
 Export functionality for admin reports
 Integration with campus directory
 Multi-language support

🐛 Known Issues

File upload size limited to 2MB (configurable)
Search functionality is case-sensitive

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.
👨‍💻 Author
Ojo Kayode Julius
📍 Lagos/Ibadan, Nigeria
🎓 B.Sc. Computer Science, University of Ibadan
🔗 GitHub: @kayodeo1
🙏 Acknowledgments

Built as part of software engineering coursework
Thanks to the JSF and PrimeFaces communities for excellent documentation
Inspired by real campus lost and found challenges


⭐ Star this repository if you find it helpful!
