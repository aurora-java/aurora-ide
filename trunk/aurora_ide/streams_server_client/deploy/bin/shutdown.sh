CLASSPATH=.
HOME=`dirname $PWD`
#echo $HOME

for jar in `ls $HOME/WEB-INF/lib/*.jar` 
do
   CLASSPATH="$CLASSPATH:""$jar" 
done
#echo $CLASSPATH
java -cp $CLASSPATH  aurora.application.admin.StopCommand 18080
#echo '------------------------------------------------------'
#echo '³É¹¦¹Ø±Õ'