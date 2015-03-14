ConfErr 
---------

This repository contains the code describe in the following papers:

[L. Keller, P. Upadhyaya, and G. Candea, **ConfErr: A Tool for Assessing Resilience to
Human Configuration Errors**, in Proc. International Conference on Dependable Systems 
and Networks (DSN), 2008, pp. 157-166.]
(http://lorenzo.nodo.ch/media/files/papers/dsn08-conferr.pdf)

**Abstract**:

We present ConfErr, a tool for testing and quantifying the resilience of software 
systems to human-induced configuration errors. ConfErr uses human error models
rooted in psychology and linguistics to generate realistic configuration mistakes; 
it then injects these mistakes and measures their effects, producing a resilience 
profile of the system under test. The resilience profile, capturing succinctly how 
sensitive the target software is to different classes of configuration errors, can 
be used for improving the software or to compare systems to each other. ConfErr is 
highly portable, because all mutations are performed on abstract representations of 
the configuration files. Using ConfErr, we found several serious flaws in the MySQL 
and Postgres databases, Apache web server, and BIND and djbdns name servers; we were 
also able to directly compare the resilience of functionally-equivalent systems, such
as MySQL and Postgres. 

## Arugula

On the branch `arugula` you can find the code described in:

[S. Andrica, L. Keller, and G. Candea, **Arugula: A programming language for describing human errors**, in Proc. EUROSYS Conference Posters and Demo Session, 2010.](http://lorenzo.nodo.ch/media/files/papers/eurosys10-arugula.pdf)
