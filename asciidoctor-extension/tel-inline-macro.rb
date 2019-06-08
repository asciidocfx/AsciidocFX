# An inline macro that turns URIs with the tel: URI scheme into links.
#
# Usage
#
#   tel:1-800-555-1212[]
#
Asciidoctor::Extensions.register do
  inline_macro do
    named :tel
    parse_content_as :text
    process do |parent, target, attrs|
      if (text = attrs['text']).empty?
        text = target
      end
      target = %(tel:#{target})
      (create_anchor parent, text, type: :link, target: target)
    end
  end
end